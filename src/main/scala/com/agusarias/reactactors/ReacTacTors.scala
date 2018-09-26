package com.agusarias.reactactors

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.agusarias.reactactors.Match.GetState
import com.agusarias.reactactors.Matches.GetMatch
import MatchJsonProtocol._

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.io.StdIn

object ReacTacTors extends App {
  implicit val system: ActorSystem = ActorSystem("reactactors")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(400.millis)

  val matchesSupervisor = system.actorOf(Matches.props, "MatchesSupervisor")

  val route =
    get {
      pathPrefix("match" / LongNumber) { id =>
        // TODO
        // - not found
        // - move
        // - calculate winner
        // - delete
        // - refactor

        val futureMatchState = for {
          Some(matchActor) <- (matchesSupervisor ? GetMatch(id)).mapTo[Option[ActorRef]]
          matchState <- (matchActor ? GetState).mapTo[MatchState]
        } yield matchState

        onSuccess(futureMatchState) {
          case matchState => complete(matchState)
          case _ => complete(StatusCodes.NotFound)
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}


