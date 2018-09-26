package com.agusarias.reactactors

import akka.actor.{Actor, ActorLogging, Props}
import com.agusarias.reactactors.Match.GetState

object Match {
  def props(id: Long) : Props = Props(new Match(MatchState.createForId(id)))

  sealed trait Message
  object GetState extends Message

  sealed trait Event
}

class Match(val state: MatchState) extends Actor with ActorLogging  {
  override def receive: Receive = {
    case GetState => sender() ! state
  }
}

object MatchState {
  def createForId(id: Long) :MatchState = MatchState(id, Board.empty, Circle, NoPlayer)
}
case class MatchState(id: Long, board: Board, next: Player, winner: Player)

case class Player(niceName: String)
object Cross extends Player("X")
object Circle extends Player("O")
object NoPlayer extends Player("-")

object Board {
  val PositionAmount = 9
  def empty: Board = new Board(List.fill(PositionAmount)(NoPlayer))
}
case class Board(positions: List[Player])