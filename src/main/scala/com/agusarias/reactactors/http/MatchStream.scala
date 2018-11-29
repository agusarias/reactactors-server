package com.agusarias.reactactors.http

import akka.pattern.ask
import akka.NotUsed
import akka.actor.ActorRef
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink}
import akka.util.Timeout
import com.agusarias.reactactors.Match.GetState
import com.agusarias.reactactors.{Board, MatchState}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object MatchStream {
  implicit val timeout: Timeout = Timeout(400.millis)

  def create(futureMatch: Future[ActorRef])(implicit m: Materializer, ec: ExecutionContext): Flow[Message, Message, NotUsed] = {
    Flow[Message].map[TextMessage] {
      case tm: TextMessage => tm
      case bm: BinaryMessage =>
        // ignore binary messages but drain content to avoid the stream being clogged
        bm.dataStream.runWith(Sink.ignore)
        TextMessage.apply("")
    }.mapAsync(parallelism = 1) { tm: TextMessage =>
      tm.toStrict(400.millis)(m).flatMap { msg =>
        msg.text match {
          case "state" =>
            futureMatch.flatMap { aMatch =>
              (aMatch ? GetState).map {
                case state: MatchState => TextMessage(MatchStateFormat(state))
              }
            }
          case _ =>
            Future.successful(TextMessage("-"))
        }
      }
    }
  }
}

object MatchStateFormat {
  private def formatBoard(board: Board): String = {
    board.positions
      .foldLeft("[")(_ + _.code + "," )
      .reverse
      .replaceFirst(",", "")
      .reverse
      .concat("]")
  }

  def apply(matchState: MatchState): String = {
    s"""{
      |   "code": ${matchState.code},
      |    "board": ${formatBoard(matchState.board)},
      |    "next": ${matchState.next.code},
      |    "winner": ${matchState.winner.code}
      | }""".stripMargin
  }
}