package poker.model.omaha

import poker.model.common._

final case class OmahaInput(board: Board, hands: List[OmahaHand]) {
  def allCombinations: Map[OmahaHand, List[Hand]] = {
    val boardCombinations = board.cards.combinations(3).toList
    val handCombinations  = hands.map(oh => oh -> oh.cards.combinations(2).toList).toMap

    handCombinations.map {
      case (k, v) => k -> v.flatMap(b => boardCombinations.map(_ ++ b)).map(Hand)
    }
  }
}

object OmahaInput {

  def make(input: String): Option[OmahaInput] =
    for {
      board <- Board.make(input.split(" ")(0))
      hands <- {
        val hands = input.split(" ").tail.toList.map(OmahaHand.make)
        val valid = hands.forall(_.isDefined)

        if (valid)
          Some(hands.map(_.get))
        else
          None
      }
    } yield OmahaInput(board, hands)
}
