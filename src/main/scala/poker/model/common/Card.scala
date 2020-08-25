package poker.model.common

final case class Card(rank: Rank, suit: Suit) {
  override def toString = rank.toString + suit.toString
}

object Card {

  def make(input: List[Char]): Option[Card] =
    for {
      rank <- Rank(input.head)
      suit <- Suit(input.last)
    } yield Card(rank, suit)
}
