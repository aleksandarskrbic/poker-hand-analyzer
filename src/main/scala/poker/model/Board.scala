package poker.model

final case class Board(cards: List[Card])

object Board {

  def make(input: String): Option[Board] = {
    val cards = input.toList.sliding(2, 2).map(Card.make).toList
    val valid = cards.length == 5 && cards.forall(_.isDefined)

    if (valid) Some(Board(cards.map(_.get)))
    else None
  }
}
