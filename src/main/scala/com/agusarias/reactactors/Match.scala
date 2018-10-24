package com.agusarias.reactactors

import akka.actor.{Actor, ActorLogging, Props}
import com.agusarias.reactactors.Match.{GetState, MakeMove}

object Match {
  def props(id: Long): Props = Props(new Match(MatchState.createForId(id)))

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
  def createForId(id: Long): MatchState = MatchState(id, Board.empty, Circle, NoPlayer)
}

case class MatchState(id: Long, board: Board, next: Player, winner: Player) {
  def move(position: Int): MatchState = {
    winner match {
      case NoPlayer =>
        val updatedBoard = board.update(position, next)
        MatchState(
          id,
          updatedBoard,
          next.other,
          updatedBoard.winner)
      case _ => this
    }
  }
}

class MatchNotFoundException(id: Long) extends Exception(s"Match with id $id not found")