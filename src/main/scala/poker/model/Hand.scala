package poker.model

final case class Hand(cards: List[Card]) {
  def isFlush: Boolean = cards.map(_.suit).distinct.size == 1

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
