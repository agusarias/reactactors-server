package com.agusarias.reactactors

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, DeserializationException, JsArray, JsString, JsValue, RootJsonFormat}

object MatchJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit object PlayerJsonFormat extends RootJsonFormat[Player] {
    def write(p: Player) = JsString(p.niceName)
    def read(value: JsValue) = Player(value.convertTo[String])
  }
  implicit object BoardJsonFormat extends RootJsonFormat[Board] {
    def write(b: Board) =
      JsArray(b.positions.map(_.niceName).map(JsString(_)))

    def read(value: JsValue): Board = value match {
      case JsArray(elements) if elements.length == Board.PositionAmount =>
        Board(elements.map(PlayerJsonFormat.read))
      case _ => throw DeserializationException("Board fields parsing error")
    }
  }
  implicit val matchStateFormat: RootJsonFormat[MatchState] = jsonFormat4(MatchState.apply)
}
