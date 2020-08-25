package poker.model.common

sealed trait Suit

object Suit {
  final case object Diamond extends Suit {
    override def toString = "d"
  }

  final case object Spade extends Suit {
    override def toString = "s"
  }

  final case object Heart extends Suit {
    override def toString = "h"
  }

  final case object Club extends Suit {
    override def toString = "c"
  }

  def apply(input: Char): Option[Suit] =
    input match {
      case 'd' => Some(Diamond)
      case 's' => Some(Spade)
      case 'h' => Some(Heart)
      case 'c' => Some(Club)
      case _   => None
    }
}
