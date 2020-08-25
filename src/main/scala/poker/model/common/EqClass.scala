package poker.model.common

final case class EqClass(eqClass: String, isFlush: Boolean, isStraightFlush: Boolean)

object EqClass {

  def make(hand: Hand): EqClass =
    EqClass(hand.toEqClass, hand.isFlush, hand.isStraightFlush)

  def make(row: Array[String]): EqClass =
    (row.slice(5, 10).mkString, row(10)) match {
      case (eqClass, "SF") => EqClass(eqClass, isFlush = true, isStraightFlush = true)
      case (eqClass, "F")  => EqClass(eqClass, isFlush = true, isStraightFlush = false)
      case (eqClass, _)    => EqClass(eqClass, isFlush = false, isStraightFlush = false)
    }
}
