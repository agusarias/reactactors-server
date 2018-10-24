package com.agusarias.reactactors

import org.scalatest.{FlatSpec, Matchers}

class MatchStateSpec extends FlatSpec with Matchers {

  "a match" should "be created empty for a given id" in {
    val aMatch = MatchState.createForId(1)

    assert(aMatch.id == 1)
    assert(aMatch.board.equals(Board.empty))
    assert(aMatch.next.isInstanceOf[Player])
    assert(aMatch.winner.equals(NoPlayer))
  }

  it should "not update the winner when a non winning move is made" in {
    val aMatch = MatchState.createForId(1)
      .move(1)

    assert(aMatch.winner.equals(NoPlayer))
  }

  it should "update it's winner when a winner move is made" in {
    val aMatch = MatchState.createForId(1)
      .move(0) // O
      .move(1) // X
      .move(3) // O
      .move(4) // X
      .move(6) // O winner move

    assert(aMatch.winner.equals(Circle))
  }
}
