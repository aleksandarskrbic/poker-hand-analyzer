package poker.model.omaha

import poker.model.common._

final case class OmahaHand(cards: List[Card]) {
  override def toString = cards.map(_.toString).mkString
}

object OmahaHand {

  def make(input: String): Option[OmahaHand] = {
    val cards = input.toList.sliding(2, 2).map(Card.make).toList
    val valid = cards.length == 4 && cards.forall(_.isDefined)

    if (valid)
      Some(OmahaHand(cards.map(_.get)))
    else
      None
  }
}
