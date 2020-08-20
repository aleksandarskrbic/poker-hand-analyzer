package poker.model

final case class EqClass(eqClass: String, isFlush: Boolean)

object EqClass {

  def make(hand: Hand): EqClass =
    EqClass(hand.toEqClass, hand.isFlush)

  def make(row: Array[String]): EqClass =
    (row.slice(5, 10).mkString, row(10)) match {
      case (eqClass, "F") => EqClass(eqClass, isFlush = true)
      case (eqClass, _)   => EqClass(eqClass, isFlush = false)
    }
}
