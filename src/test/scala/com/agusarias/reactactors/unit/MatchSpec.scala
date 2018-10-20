package com.agusarias.reactactors.unit

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import akka.util.Timeout
import com.agusarias.reactactors.Match
import com.agusarias.reactactors.Match.GetState
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.language.postfixOps

class MatchSpec(_system: ActorSystem)
  extends TestKit(_system)
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("MatchSpec"))
  implicit val timeout: Timeout = Timeout(400 millis)

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A Match Actor" should {
    "retrieve its state" in {
      val receiver = TestProbe()
      val someId = 3
      val aMatch = system.actorOf(Match.props(someId))
      aMatch.tell(GetState, receiver.ref)
      receiver.expectMsgType[Long](500 millis)
    }
  }
}
