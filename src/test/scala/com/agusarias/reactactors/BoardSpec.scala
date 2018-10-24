package com.agusarias.reactactors

import org.scalatest.{FlatSpec, Matchers}

class BoardSpec extends FlatSpec with Matchers {
  "A board" should "An empty board is created ok" in {
    val board = Board.empty

    assert(board.positions.length == Board.PositionAmount)
    assert(board.positions.forall(_.equals(NoPlayer)))
  }

  it should "An board is updated with no winner correctly" in {
    val board = Board.empty.update(0, Circle)

    assert(board.positions.length == Board.PositionAmount)
    assert(board.positions(0).equals(Circle))
    assert(board.positions.count(_.equals(NoPlayer)) == Board.PositionAmount - 1)
    assert(board.winner.equals(NoPlayer))
  }

  it should "not allow repeated positions" in {
    a[AssertionError] should be thrownBy {
      Board.empty
        .update(0, Circle)
        .update(0, Circle)
    }
  }

  it should "not allow non existent positions" in {
    a[AssertionError] should be thrownBy {
      Board.empty.update(-1, Circle)
    }
    a[AssertionError] should be thrownBy {
      Board.empty.update(100, Circle)
    }
  }

  it should "An board is updated with a winner correctly" in {
    val board = Board.empty
      .update(0, Circle)
      .update(1, Circle)
      .update(2, Circle)

    assert(board.positions.length == Board.PositionAmount)
    assert(board.positions(0).equals(Circle))
    assert(board.positions.count(_.equals(NoPlayer)) == Board.PositionAmount - 3)
    assert(board.winner.equals(Circle))
  }
}
