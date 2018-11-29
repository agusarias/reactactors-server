package com.agusarias.reactactors

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.agusarias.reactactors.http.{CorsHandler, MatchExceptionHandler, MatchRoutes}

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Main extends App
  with MatchRoutes
  with MatchExceptionHandler
  with CorsHandler {
  override implicit val system: ActorSystem = ActorSystem("reactactors")
  override implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  override implicit val materializer: ActorMaterializer = ActorMaterializer()

  override val matches: ActorRef = system.actorOf(Matches.props, "MatchesSupervisor")

  val routes = handleCors(matchRoutes)
  val bindingFuture = Http().bindAndHandle(routes, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}


