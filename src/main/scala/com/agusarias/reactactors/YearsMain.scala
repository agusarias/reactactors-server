package com.agusarias.reactactors

object YearsMain extends App {
  val people = (1900 -> 1923) ::
    (1920 -> 1950) ::
    (1940 -> 1950) ::
    (1945 -> 1955) ::
    (1955 -> 1990) ::
    (1955 -> 2001) ::
    (1955 -> 1983) :: Nil

  val max = people.foldLeft(Map.empty[Int, Int]) {
    case (updatedYears, (from, to)) => from.to(to).foldLeft(updatedYears) {
      (theYears, year) => theYears.updated(year, theYears.getOrElse(year, 0) + 1)
    }
  }.reduce((oneYear, otherYear) => if (oneYear._2 > otherYear._2) oneYear else otherYear)

  println(max)
}


