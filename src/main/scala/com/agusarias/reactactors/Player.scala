package com.agusarias.reactactors

abstract case class Player(niceName: String) {
  def other: Player

  override def toString: String = niceName
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
