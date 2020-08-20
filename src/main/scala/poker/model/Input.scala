package poker.model

final case class Input(board: Board, hands: List[StartHand]) {
  def allCards: Map[StartHand, List[Card]] =
    hands.map(_ -> board.cards).toMap.map { case (k, v) => k -> (List(k.first, k.second) ++ v) }

  def allCombinations: Map[StartHand, List[Hand]] = // allCards.combinations(5).map(Hand).toList
    allCards.map { case (k, v) => k -> v.combinations(5).map(Hand).toList }
}

object Input {

  def make(input: String): Option[Input] =
    for {
      board <- Board.make(input.split(" ")(0))
      hands <- {
        val hands = input.split(" ").tail.toList.map(StartHand.make)

        if (!hands.exists(_.isEmpty))
          Some(hands.map(_.get))
        else
          None
      }
    } yield Input(board, hands)
}
