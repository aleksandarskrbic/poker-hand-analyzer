package poker

import zio._

package object game {

  def shouldQuit(text: String): UIO[Boolean] =
    ZIO.succeed(text.isEmpty)

  def prettyPrint(output: List[List[String]]): String =
    output
      .map {
        case h :: Nil => h
        case cards    => cards.mkString(" ", "=", "")
      }
      .mkString(" ")
      .trim
}
