package com.agusarias.reactactors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Status}
import akka.pattern.ask
import akka.util.Timeout
import com.agusarias.reactactors.Match.GetState

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}


object Matches {
  implicit val timeout: Timeout = Timeout(400.millis)

  def props: Props = Props(new Matches)

  sealed trait Message

  object CreateMatch extends Message

  case class GetMatch(id: Long) extends Message

  object GetMatches extends Message

  case class DeleteMatch(id: Long) extends Message

  sealed trait Event

  case class MatchCreated(id: Long) extends Event

  case class MatchDeleted(id: Long) extends Event

}

class Matches() extends Actor with ActorLogging {

  import Matches._
  import context._

  var state: MatchesState = MatchesState(Map.empty[Long, ActorRef])

  override def receive: Receive = {
    case CreateMatch =>
      sender() ! MatchCreated(createMatch())
    case GetMatch(id) =>
      sender() ! getMatch(id)
    case GetMatches =>
      val originalSender = sender()
      val futures: Future[List[MatchState]] = getMatches

      futures.onComplete {
        case Success(result) => originalSender ! result
        case Failure(_) => originalSender ! Status.Failure
      }

    case DeleteMatch(id) =>
      deleteMatch(id)
      sender() ! MatchDeleted(id)
  }

  def createMatch(): Long = {
    createMatchWithId(nextId)
  }

  def createMatchWithId(newId: Long): Long = {
    val newMatch = context.actorOf(Match.props(newId))
    state = state.copy(matches = state.matches + (newId -> newMatch))
    newId
  }

  def deleteMatch(id: Long) {
    state = state.copy(matches = state.matches.filterNot {
      case (anId, _) => anId == id
    })
  }

  def getMatch(id: Long): Option[ActorRef] = {
    state.matches.get(id)
  }

  def getMatches: Future[List[MatchState]] = {
    for {
      matchActor <- state.matches.values
      matchState = (matchActor ? GetState).mapTo[MatchState]
    } yield matchState

    val futures = state.matches.values.map({
      matchActor => (matchActor ? GetState).mapTo[MatchState]
    }).toList

    Future.sequence(futures)
  }

  def nextId: Long = state.matches.size + 1
}

case class MatchesState(matches: Map[Long, ActorRef])