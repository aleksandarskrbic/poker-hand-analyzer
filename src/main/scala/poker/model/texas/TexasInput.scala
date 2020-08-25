package poker.model.texas

import poker.model.common._

final case class TexasInput(board: Board, hands: List[TexasHand]) {
  private val allCards: Map[TexasHand, List[Card]] =
    hands.map(_ -> board.cards).toMap.map { case (k, v) => k -> (List(k.first, k.second) ++ v) }

  def allCombinations: Map[TexasHand, List[Hand]]  =
    allCards.map { case (k, v) => k -> v.combinations(5).map(Hand).toList }
}

object TexasInput {

  def make(input: String): Option[TexasInput] =
    for {
      board <- Board.make(input.split(" ")(0))
      hands <- {
        val hands = input.split(" ").tail.toList.map(TexasHand.make)
        val valid = hands.forall(_.isDefined)

        if (valid) Some(hands.map(_.get))
        else None
      }
    } yield TexasInput(board, hands)
}
