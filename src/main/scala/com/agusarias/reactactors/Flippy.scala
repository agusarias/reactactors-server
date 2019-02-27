package com.agusarias.reactactors

object Flippy extends App {
  def flippy(input: String): String = {
    var cards = input.toCharArray.toList
    var steps = List.empty[Int]

    while (!cards.forall(_ == '.') && cards.contains('1')){
      val (_cards, _steps) = removeNext(cards, steps)
      cards = _cards
      steps = _steps
      println(cards.mkString(" "))
    }

    if(cards.contains('0')){
      "No solution"
    } else {
     steps.mkString(" ")
    }
  }

  def removeNext(cards: List[Char], steps: List[Int]): (List[Char], List[Int]) = {
    val first = cards.indexOf('1')
    var updated = cards.updated(first, '.')
    if(first > 0){object Flippy extends App {
      def flippy(input: String): String = {
        var cards = input.toCharArray.toList
        var steps = List.empty[Int]

        while (!cards.forall(_ == '.') && cards.contains('1')){
          val (_cards, _steps) = removeNext(cards, steps)
          cards = _cards
          steps = _steps
          println(cards.mkString(" "))
        }

        if(cards.contains('0')){
          "No solution"
        } else {
          steps.mkString(" ")
        }
      }

      def removeNext(cards: List[Char], steps: List[Int]): (List[Char], List[Int]) = {
        val first = cards.indexOf('1')
        var updated = cards.updated(first, '.')
        if(first > 0){
          updated = updated.updated(first - 1, invert(cards(first - 1)))
        }
        if(first < cards.length -1){
          updated = updated.updated(first + 1, invert(cards(first + 1)))
        }
        (updated, steps :+ first)
      }

      def invert(char: Char) = if(char == '.') '.' else if (char == '1') '0' else '1'

      //  println(flippy("0100110"))
      //  println(flippy("01001100111"))
      //  println(flippy("100001100101000"))
      println(flippy("010111111111100100101000100110111000101111001001011011000011000"))
    }
      updated = updated.updated(first - 1, invert(cards(first - 1)))
    }
    if(first < cards.length -1){
      updated = updated.updated(first + 1, invert(cards(first + 1)))
    }
    (updated, steps :+ first)
  }

  def invert(char: Char) = if(char == '.') '.' else if (char == '1') '0' else '1'

//  println(flippy("0100110"))
//  println(flippy("01001100111"))
//  println(flippy("100001100101000"))
  println(flippy("010111111111100100101000100110111000101111001001011011000011000"))
}
