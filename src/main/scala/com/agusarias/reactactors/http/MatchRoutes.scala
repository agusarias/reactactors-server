package com.agusarias.reactactors.http

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.agusarias.reactactors.Match.GetState
import com.agusarias.reactactors.MatchState
import com.agusarias.reactactors.Matches.{CreateMatch, GetMatch, GetMatches, MatchCreated}
import com.agusarias.reactactors.MatchJsonProtocol._

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._

trait MatchRoutes {
  implicit def system: ActorSystem
  implicit val executionContext: ExecutionContextExecutor
  implicit val timeout: Timeout = Timeout(400.millis)

  lazy val log = Logging(system, classOf[MatchRoutes])
  val matchesSupervisor: ActorRef

  def matchRoutes: Route =
    pathPrefix("match" / LongNumber) {
      id =>
        get {
          val futureMatchState: Future[Option[MatchState]] =
            (matchesSupervisor ? GetMatch(id)).mapTo[Option[ActorRef]].flatMap {
              case Some(matchActor) => (matchActor ? GetState).mapTo[MatchState].map(Some(_))
              case None => Future.successful(None)
            }

          onSuccess(futureMatchState) {
            case Some(matchState) => complete(matchState)
            case None => complete(StatusCodes.NotFound)
          }
          //      } ~ put {
          //        (matchesSupervisor ? MakeMove(id)).mapTo[Option[ActorRef]]
        }
    } ~ path("match") {
      post {
        val futureResult = matchesSupervisor ? CreateMatch
        onSuccess(futureResult) {
          case MatchCreated(id) => complete(s"$id")
          case _ => complete(StatusCodes.BadRequest)
        }
      } ~ get {
        val futureMatches = (matchesSupervisor ? GetMatches).mapTo[List[MatchState]]
        onSuccess(futureMatches) {
          case matches: List[MatchState] => complete(matches)
          case _ => complete(StatusCodes.BadRequest)
        }
      }
    }
}
