package com.agusarias.reactactors

abstract case class Player(code: Int, niceName: String) {
  def other: Player

  override def toString: String = niceName
}

object Player {
  def of(code: Int): Player = code match {
    case 2 => Cross
    case 1 => Circle
    case _ => NoPlayer
  }
}

object Cross extends Player(2, "X") {
  override def other: Circle.type = Circle
}

object Circle extends Player(1, "O") {
  override def other: Player = Cross
}

object NoPlayer extends Player(0, "-") {
  override def other: Player = NoPlayer
}
