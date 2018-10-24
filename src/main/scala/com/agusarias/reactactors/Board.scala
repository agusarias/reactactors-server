package com.agusarias.reactactors

object Board {
  val PositionAmount = 9

  def empty: Board = new Board(Vector.fill(PositionAmount)(NoPlayer))
}

case class Board(positions: Vector[Player]) {
  def update(position: Int, next: Player): Board = {
    if(!(0 to Board.PositionAmount).contains(position))
      throw new InvalidMoveException(s"Invalid position $position")

    if(!positions(position).equals(NoPlayer))
      throw new InvalidMoveException(s"Trying to move player $next into unavailable position $position")

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

class InvalidMoveException(msg: String) extends Exception(msg)
