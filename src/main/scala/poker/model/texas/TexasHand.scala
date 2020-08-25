package poker.model.texas

import poker.model.common._

final case class TexasHand(first: Card, second: Card) {
  override def toString = first.toString + second.toString
}

object TexasHand {

  def make(input: String): Option[TexasHand] =
    if (input.length == 4)
      for {
        first  <- Card.make(input.substring(0, 2).toList)
        second <- Card.make(input.substring(2, 4).toList)
      } yield TexasHand(first, second)
    else None
}
