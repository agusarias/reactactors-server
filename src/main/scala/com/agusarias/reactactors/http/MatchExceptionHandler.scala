package com.agusarias.reactactors.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import com.agusarias.reactactors.{InvalidMoveException, MatchNotFoundException}

trait MatchExceptionHandler {
  implicit val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: InvalidMoveException => complete(StatusCodes.BadRequest, e.getMessage)
    case e: MatchNotFoundException => complete(StatusCodes.NotFound, e.getMessage)
  }
}
