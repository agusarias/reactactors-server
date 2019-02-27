package com.agusarias.reactactors

object EndNode extends Node('@', List.empty) {
  override def padded(level: Int, avoidTabs: Boolean): String = "END"
  override def isEnd: Boolean = true
}

object Node {
  def apply(char: Char): Node = Node(char, List.empty)

  def apply(char: Char, node: Node): Node = Node(char, List(node))
}

case class Node(char: Char, children: List[Node]) {

  def padded(level: Int, avoidTabs: Boolean = false): String = {
    val start = (if (avoidTabs) "\t" * level else "") + s"$char -> "
    children match {
      case List(one) => start + one.padded(level, avoidTabs)
      case aList => start + aList.map("\n" + _.padded(level + 1)).foldLeft("")(_ + _)
    }
  }

  def isEnd: Boolean = false
}

case class Trie(nodes: List[Node]) {
  def contains(word: String): Boolean = {
    recContain(word, nodes)
  }

  def recContain(word: String, nodes: List[Node]): Boolean = {
    if (word.length == 0)
      return nodes.exists(node => node.isEnd)
    val char = word.charAt(0)
    val nextNode: Option[Node] = nodes.find(node => node.char == char)
    nextNode.exists(node => recContain(word.drop(1), node.children))
  }

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
                Node(char, generateNodes(node.children, chars.tail)))
            case None =>
              curNodes :+ Node(char, generateNodes(List.empty, chars.tail))
          }
      }
    }

    Trie(generateNodes(nodes, word))
  }
}

object AnotherMain extends App {
  def funnel(validWords: Trie, word: String): Int = {
    // TODO use cache

    if (exists(validWords, word)) {
      println(s"Valid word $word")
    } else {
      return 0
    }

    val newWords = 1.to(word.length)
      .map(index => word.take(index - 1) + word.drop(index))

    if (newWords.isEmpty)
      1
    else
      1 + newWords.map(funnel(validWords, _)).max
  }

  def exists(validWords: Trie, word: String): Boolean = validWords.contains(word)

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
      |gnash
      |nash
      |ash
      |ah
    """.stripMargin.lines.foldLeft(Trie(List.empty)) {
      (trie, word) => trie.insert(word)
    }

  println(funnel(myTrie, "gnash"))
}
