package com.agusarias.reactactors.unit

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import akka.util.Timeout
import com.agusarias.reactactors.Matches
import com.agusarias.reactactors.Matches._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.language.postfixOps

class MatchesSpec(_system: ActorSystem)
  extends TestKit(_system)
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("MatchesSpec"))
  implicit val timeout: Timeout = Timeout(400 millis)

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A Matches Actor" should {
    "create a match" in {
      val receiver = TestProbe()

      val matches = system.actorOf(Matches.props)
      matches.tell(CreateMatch, receiver.ref)
      receiver.expectMsg(500 millis, MatchCreated(1))
    }

    "retrieve an existent match" in {
      val receiver = TestProbe()
      val matches = system.actorOf(Matches.props)
      matches ! CreateMatch
      matches.tell(GetMatch(1), receiver.ref)
      receiver.expectMsgType[Some[ActorRef]](500 millis)
    }

    "return none retrieving non existent match" in {
      val receiver = TestProbe()
      val matches = system.actorOf(Matches.props)
      matches.tell(GetMatch(1), receiver.ref)
      receiver.expectMsg(500 millis, None)
    }

    "remove an existing match" in {
      val deleteReceiver = TestProbe()
      val getReceiver = TestProbe()
      val matches = system.actorOf(Matches.props)
      matches ! CreateMatch //  1
      matches ! CreateMatch //  2
      matches ! CreateMatch //  3

      // Exists
      matches.tell(GetMatch(2), getReceiver.ref)
      getReceiver.expectMsgType[Some[ActorRef]](500 millis)

      // Is removed
      matches.tell(DeleteMatch(2), deleteReceiver.ref)
      deleteReceiver.expectMsg(500 millis, MatchDeleted(2))

      // Cease to exists
      matches.tell(GetMatch(2), getReceiver.ref)
      getReceiver.expectMsg(500 millis, None)
    }
  }
}
