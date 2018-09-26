package com.agusarias.reactactors

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object MatchJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val playerFormat: RootJsonFormat[Player] = jsonFormat1(Player.apply)
  implicit val boardFormat: RootJsonFormat[Board] = jsonFormat1(Board.apply)
  implicit val matchStateFormat: RootJsonFormat[MatchState] = jsonFormat4(MatchState.apply)
}
