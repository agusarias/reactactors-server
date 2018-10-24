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

class Match(val state: MatchState) extends Actor with ActorLogging {
  override def receive: Receive = {
    case GetState => sender() ! state
    case MakeMove(position) => {
      //      state = state.move(position)
      sender() ! state
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

abstract case class Player(niceName: String) {
  def other: Player
}

object Player {
  def of(name: String): Player = name match {
    case "X" => Cross
    case "O" => Circle
    case _ => NoPlayer
  }
}

object Cross extends Player("X") {
  override def other: Circle.type = Circle
}

object Circle extends Player("O") {
  override def other: Player = Cross
}

object NoPlayer extends Player("-") {
  override def other: Player = NoPlayer
}

object Board {
  val PositionAmount = 9

  def empty: Board = new Board(Vector.fill(PositionAmount)(NoPlayer))
}

case class Board(positions: Vector[Player]) {
  def update(position: Int, next: Player): Board = {
    assert((0 to Board.PositionAmount).contains(position))
    assert(positions(position).equals(NoPlayer))

    Board(positions.updated(position, next))
  }

  def winner: Player = {
    // TODO generate based on PositionCount
    val winningLines = Seq(
      // Horizontal
      Seq(0, 1, 2),
      Seq(3, 4, 5),
      Seq(6, 7, 8),
      // Vertical
      Seq(0, 3, 6),
      Seq(1, 4, 7),
      Seq(2, 5, 8),
      // Diagonal
      Seq(0, 4, 8),
      Seq(2, 4, 6)
    )

    val winningLine = winningLines
      .find(wc => wc.forall(positions(_).equals(positions(wc.head))))

    winningLine match {
      case Some(Seq(position, _, _)) => positions(position)
      case None => NoPlayer
    }
  }
}