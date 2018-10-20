package com.agusarias.reactactors

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.agusarias.reactactors.http.MatchRoutes

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Main extends App with MatchRoutes {
  override implicit val system: ActorSystem = ActorSystem("reactactors")
  override implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  override val matchesSupervisor: ActorRef = system.actorOf(Matches.props, "MatchesSupervisor")

  // TODO
  // - move
  // - calculate winner
  // - delete

  val routes = matchRoutes
  val bindingFuture = Http().bindAndHandle(routes, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}


