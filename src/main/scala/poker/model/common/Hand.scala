package poker.model.common

final case class Hand(cards: List[Card]) {
  def isFlush: Boolean = cards.map(_.suit).distinct.size == 1

  def isStraightFlush: Boolean = {
    val sorted     = cards.map(_.rank.value).sorted
    val diff       = sorted.last - sorted.head
    val otherCards = sorted.drop(1).dropRight(1).filter(a => a > sorted.head && a < sorted.last)

    isFlush match {
      case true => diff == 4 && otherCards.size == 3
      case _    => false
    }
  }

  def toEqClass: String =
    cards
      .groupBy(_.rank)
      .toSeq
      .sortBy(row => (row._2.size, row._1.value))
      .reverse
      .flatMap(_._2)
      .map(_.rank.toString)
      .mkString

}
