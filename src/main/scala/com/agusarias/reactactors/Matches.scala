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

  case class GetMatch(code: Long) extends Message

  object GetMatches extends Message

  case class DeleteMatch(code: Long) extends Message

  sealed trait Event

  case class MatchCreated(code: Long) extends Event

  case class MatchDeleted(code: Long) extends Event

}

class Matches() extends Actor with ActorLogging {

  import Matches._
  import context._

  var state: MatchesState = MatchesState(Map.empty[Long, ActorRef])

  override def receive: Receive = {
    case CreateMatch =>
      sender() ! createMatch()
    case GetMatch(code) =>
      sender() ! getMatch(code)
    case GetMatches =>
      val originalSender = sender()
      val futures: Future[List[MatchState]] = getMatches

      futures.onComplete {
        case Success(result) => originalSender ! result
        case Failure(_) => originalSender ! Status.Failure
      }

    case DeleteMatch(code) =>
      deleteMatch(code)
      sender() ! MatchDeleted(code)
  }

  def createMatch(): ActorRef = {
    createMatchWithId(nextId)
  }

  def createMatchWithId(newId: Long): ActorRef = {
    val newMatch = context.actorOf(Match.props(newId))
    state = state.copy(matches = state.matches + (newId -> newMatch))
    newMatch
  }

  def deleteMatch(code: Long) {
    state = state.copy(matches = state.matches.filterNot {
      case (anId, _) => anId == code
    })
  }

  def getMatch(code: Long): Option[ActorRef] = {
    state.matches.get(code)
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