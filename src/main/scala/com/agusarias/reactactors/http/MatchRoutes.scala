package com.agusarias.reactactors.http

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.agusarias.reactactors.Match.{GetState, MakeMove}
import com.agusarias.reactactors.{MatchNotFoundException, MatchState}
import com.agusarias.reactactors.Matches.{CreateMatch, GetMatch, GetMatches}
import com.agusarias.reactactors.http.MatchJsonProtocol._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

trait MatchRoutes {
  implicit def system: ActorSystem

  implicit val executionContext: ExecutionContextExecutor
  implicit val timeout: Timeout = Timeout(400.millis)

  lazy val log = Logging(system, classOf[MatchRoutes])
  val matches: ActorRef

  def matchRoutes: Route =
    pathPrefix("match" / LongNumber) {
      code =>
        val futureMatch = (matches ? GetMatch(code)).mapTo[Option[ActorRef]]
        get {
          val futureMatchState = futureMatch.flatMap {
            case Some(aMatch) => (aMatch ? GetState).mapTo[MatchState]
            case None => throw new MatchNotFoundException(code)
          }
          completeWithMatch(futureMatchState)
        } ~ (put & parameter("position".as[Int])) {
          position =>
            val futureMatchState = futureMatch.flatMap {
              case Some(aMatch) => (aMatch ? MakeMove(position)).flatMap {
                case state: MatchState => Future.successful(state)
                case t: Throwable => throw t
              }
              case None => throw new MatchNotFoundException(code)
            }
            completeWithMatch(futureMatchState)
        }
    } ~ path("match") {
      post {
        val futureMatchState = (matches ? CreateMatch).mapTo[ActorRef].flatMap {
          aMatch => (aMatch ? GetState).mapTo[MatchState]
        }
        completeWithMatch(futureMatchState)
      } ~ get {
        val futureMatches = (matches ? GetMatches).mapTo[List[MatchState]]
        onSuccess(futureMatches) {
          case matches: List[MatchState] => complete(matches)
          case _ => complete(StatusCodes.BadRequest)
        }
      }
    }

  def completeWithMatch(futureMatchState: Future[MatchState]): Route = {
    onComplete(futureMatchState) {
      case Success(matchState) => complete(matchState)
      case Failure(e) => throw e
    }
  }
}
