package com.agusarias.reactactors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}


object Matches {
  def props: Props = Props(new Matches)

  sealed trait Message

  object CreateMatch extends Message

  case class GetMatch(id: Long) extends Message

  case class DeleteMatch(id: Long) extends Message

  sealed trait Event

  case class MatchCreated(id: Long) extends Event

  case class MatchDeleted(id: Long) extends Event
}

class Matches() extends Actor with ActorLogging {

  import Matches._

  var state: MatchesState = MatchesState(Map.empty[Long, ActorRef])

  override def receive: Receive = {
    case CreateMatch =>
      sender() ! MatchCreated(createMatch())
    case GetMatch(id) =>
      sender() ! getMatch(id)
    case DeleteMatch(id) =>
      deleteMatch(id)
      sender() ! MatchDeleted(id)
  }

  def createMatch() : Long = {
    createMatchWithId(nextId)
  }

  def createMatchWithId(newId: Long) : Long = {
    val newMatch = context.actorOf(Match.props(newId))
    state = state.copy(matches = state.matches + (newId -> newMatch))
    newId
  }

  def deleteMatch(id: Long)  {
    state = state.copy(matches = state.matches.filterNot {
      case (anId, _) => anId == id
    })
  }

  def getMatch(id: Long): Option[ActorRef] = {
    // create if not exists
    if(!state.matches.isDefinedAt(id)){
      createMatchWithId(id)
    }
    state.matches.get(id)
  }

  def nextId: Long = state.matches.size + 1
}

case class MatchesState(matches: Map[Long, ActorRef])