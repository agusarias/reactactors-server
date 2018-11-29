package com.agusarias.reactactors.http

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import com.agusarias.reactactors.Match.{GetState, MakeMove}
import com.agusarias.reactactors.Matches.{CreateMatch, GetMatch, GetMatches}
import com.agusarias.reactactors.http.MatchJsonProtocol._
import com.agusarias.reactactors.{InvalidMoveException, MatchNotFoundException, MatchState, Movement}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

trait MatchRoutes {
  implicit def system: ActorSystem
  implicit def materializer: Materializer

  implicit val executionContext: ExecutionContextExecutor
  implicit val timeout: Timeout = Timeout(400.millis)

  lazy val log = Logging(system, classOf[MatchRoutes])
  val matches: ActorRef

  def matchRoutes: Route =
    pathPrefix("match" / LongNumber) {
      code =>
        val futureMaybeMatch = (matches ? GetMatch(code)).mapTo[Option[ActorRef]]
        pathPrefix("stream") {
          val futureMatch = futureMaybeMatch.map {
            case Some(aMatch) => aMatch
            case None => throw new MatchNotFoundException(code)
          }
          handleWebSocketMessages(MatchStream.create(futureMatch))
        } ~ get {
          val futureMatchState = futureMaybeMatch.flatMap {
            case Some(aMatch) => (aMatch ? GetState).mapTo[MatchState]
            case None => throw new MatchNotFoundException(code)
          }
          complete(futureMatchState)
        } ~ (put & entity(as[Movement])) {
          movement =>
            val futureMatchState = futureMaybeMatch.flatMap {
              case Some(aMatch) => (aMatch ? MakeMove(movement.position)).flatMap {
                case state: MatchState => Future.successful(state)
                case e: InvalidMoveException => throw e
              }
              case None => throw new MatchNotFoundException(code)
            }
            complete(futureMatchState)
        }
    } ~ path("match") {
      post {
        val futureMatchState = (matches ? CreateMatch).mapTo[ActorRef].flatMap {
          aMatch => (aMatch ? GetState).mapTo[MatchState]
        }
        complete(futureMatchState)
      } ~ get {
        val futureMatches = (matches ? GetMatches).mapTo[List[MatchState]]
        complete(futureMatches)
      }
    }
}
