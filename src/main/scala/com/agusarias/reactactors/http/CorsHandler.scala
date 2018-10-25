package com.agusarias.reactactors.http

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

trait CorsHandler {
  private def preflightRequestHandler: Route = options {
    complete(HttpResponse(StatusCodes.OK).
      withHeaders(
        RawHeader("Access-Control-Allow-Methods",
          "OPTIONS, POST, PUT, GET, DELETE")))
  }

  def handleCors(r: Route): Route =
    respondWithHeaders(
      RawHeader("Access-Control-Allow-Origin", "*"),
      RawHeader("Access-Control-Allow-Credentials", "true"),
      RawHeader("Access-Control-Allow-Headers",
        "Authorization, Content-Type, X-Requested-With")
    )( preflightRequestHandler ~ r)
}
