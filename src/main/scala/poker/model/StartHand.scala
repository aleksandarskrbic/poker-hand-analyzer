package poker.model

final case class StartHand(first: Card, second: Card) {
  override def toString = first.toString + second.toString
}

object StartHand {

  def make(input: String): Option[StartHand] =
    for {
      first <- Card.make(input.substring(0, 2).toList)
      second <- Card.make(input.substring(2, 4).toList)
    } yield StartHand(first, second)
}
