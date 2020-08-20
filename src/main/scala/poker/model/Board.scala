package poker.model

final case class Board(cards: List[Card])

object Board {

  def make(input: String): Option[Board] = {
    val cards       = input.toList.sliding(2, 2).map(Card.make).toList
    val containNone = cards.exists(_.isEmpty)

    if (cards.length == 5 && !containNone)
      Some(Board(cards.map(_.get)))
    else
      None
  }
}
