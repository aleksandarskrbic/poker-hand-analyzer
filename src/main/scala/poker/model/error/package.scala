package poker.model

package object error {
  sealed trait Error
  final case class InvalidInputError(text: String) extends Error
}
