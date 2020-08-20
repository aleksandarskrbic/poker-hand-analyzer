package poker

package object model {
  sealed trait Error
  final case class InvalidInputError(text: String) extends Error
}
