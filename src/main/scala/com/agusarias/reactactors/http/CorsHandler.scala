package com.agusarias.reactactors.http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

trait CorsHandler {
  def handleCors(route: Route): Route =
    respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*"))(route)
}
