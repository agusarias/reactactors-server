package com.agusarias.reactactors


object EndNode extends Node('@', List.empty) {
  override def padded(level: Int, avoidTabs: Boolean): String = "END"
}

object Node {
  def apply(char: Char): Node = Node(char, List.empty)

  def apply(char: Char, node: Node): Node = Node(char, List(node))
}

case class Node(char: Char, childs: List[Node]) {

  def padded(level: Int, avoidTabs: Boolean = false): String = {
    val start = (if (avoidTabs) "\t" * level else "") + s"$char -> "
    childs match {
      case List(one) => start + one.padded(level, avoidTabs)
      case aList => start + aList.map("\n" + _.padded(level + 1)).foldLeft("")(_ + _)
    }
  }
}

case class Trie(nodes: List[Node]) {
  override def toString: String =
    nodes.foldLeft("") {
      (str, node) => str + "\n" + node.padded(0)
    }

  def insert(word: String): Trie = {
    def generateNodes(curNodes: List[Node], chars: String): List[Node] = {
      chars match {
        case "" => curNodes :+ EndNode
        case _ =>
          val char = chars.head
          curNodes.find(_.char == char) match {
            case Some(node) =>
              curNodes.updated(curNodes.indexOf(node),
                Node(char, generateNodes(node.childs, chars.tail)))
            case None =>
              curNodes :+ Node(char, generateNodes(List.empty, chars.tail))
          }
      }
    }

    Trie(generateNodes(nodes, word))
  }
}

object AnotherMain extends App {

  def findWords(nodes: List[Node], numbers: String, strict: Boolean, wordUpToThisPoint: String = ""): List[String] = {
    if (numbers.isEmpty) return List.empty

    val number = numbers.head
    val possibleNext = numberMap(number)
    val existentNext = nodes
    val nextLetters = existentNext
      .filter(node => possibleNext.contains(node.char))

    val finished = if (strict && numbers.length != 1) List.empty else
      nextLetters
        .filter(node => node.childs.contains(EndNode))
        .map(node => wordUpToThisPoint + node.char)

    val unfinished = nextLetters
      .filter(node => node.childs.exists(!_.equals(EndNode)))

    finished ++ unfinished.flatMap(node =>
      findWords(node.childs,
        numbers.tail, strict,
        wordUpToThisPoint + node.char))
  }

  val myTrie =
    """buzz
      |hose
      |winter
      |tall
      |blade
      |explain
      |quilt
      |eatable
      |first
      |month
      |unit
      |lean
      |best
      |sleet
      |toad
      |whimsical
      |produce
      |please
      |free
      |relax
      |tramp
      |gigantic
      |slow
      |nail
      |button
      |applaud
      |grouchy
      |bumpy
      |thin
      |hissing
      |homeless
      |art
      |magical
      |analyse
      |fancy
      |smelly
      |regular
      |meaty
      |writer
      |interest
      |suggestion
      |travel
      |homely
      |moldy
      |tense
      |oven
      |tawdry
      |push
      |sad
      |handle
      |ship
      |cooperative
      |deep
      |phobic
      |versed
      |ambiguous
      |development
      |obese
      |arrest
      |mellow
      |unhealthy
      |heartbreaking
      |ludicrous
      |start
      |miss
      |boundary
      |run
      |animated
      |space
      |force
      |jaded
      |carve
      |sleep
      |damp
      |evasive
      |lumpy
      |power
      |necessary
      |acoustic
      |whistle
      |filthy
      |territory
      |momentous
      |grandmother
      |unsuitable
      |quiet
      |whirl
      |addicted
      |quirky
      |stroke
      |coil
      |loaf
      |wash
      |cowardly
      |accept
      |oil
      |highfalutin
      |word
      |subsequent
      |quicksand
      |busy
      |hateful
      |scrawny
      |dynamic
      |zany
      |crowd
      |sock
      |joyous
      |knot
      |thaw
      |work
      |cloudy
      |sugar
      |selfish
      |full
      |creepy
      |shirt
      |sticks
      |pear
      |brick
    """.stripMargin.lines.foldLeft(Trie(List.empty)) {
      (trie, word) => trie.insert(word)
    }

  val numberMap = Map.empty[Char, List[Char]] +
    ('2' -> List('a', 'b', 'c')) +
    ('3' -> List('d', 'e', 'f')) +
    ('4' -> List('g', 'h', 'i')) +
    ('5' -> List('j', 'k', 'l')) +
    ('6' -> List('m', 'n', 'o')) +
    ('7' -> List('p', 'q', 'r', 's')) +
    ('8' -> List('t', 'u', 'v')) +
    ('9' -> List('x', 'y', 'z'))

  val numbers = "8733"

  println(myTrie)

  println(findWords(myTrie.nodes, numbers, false))
}
