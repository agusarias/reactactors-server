package com.agusarias.reactactors

import akka.actor.{Actor, ActorLogging, Props}
import com.agusarias.reactactors.Match.{GetState, MakeMove}

object Match {
  def props(code: Long): Props = Props(new Match(MatchState.createForCode(code)))

  sealed trait Message

  object GetState extends Message

  case class MakeMove(position: Int) extends Message

  sealed trait Event

}

class Match(var state: MatchState) extends Actor with ActorLogging {
  override def receive: Receive = {
    case GetState => sender() ! state
    case MakeMove(position) =>
      try {
        state = state.move(position)
        sender() ! state
      } catch {
        case e: InvalidMoveException => sender() ! e
      }
  }
}

object MatchState {
  def createForCode(code: Long): MatchState = MatchState(code, Board.empty, Circle, NoPlayer)
}

case class MatchState(code: Long, board: Board, next: Player, winner: Player) {
  def move(position: Int): MatchState = {
    winner match {
      case NoPlayer =>
        val updatedBoard = board.update(position, next)
        MatchState(
          code,
          updatedBoard,
          next.other,
          updatedBoard.winner)
      case _ => this
    }
  }
}

class MatchNotFoundException(code: Long) extends Exception(s"Match with code $code not found")