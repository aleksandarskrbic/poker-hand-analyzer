package poker.model

sealed trait Rank {
  def value: Int
}

object Rank {
  final case object Ace extends Rank {
    override def toString = "A"
    override def value    = 12
  }

  final case object King extends Rank {
    override def toString = "K"
    override def value    = 11
  }

  final case object Queen extends Rank {
    override def toString = "Q"
    override def value    = 10
  }

  final case object Jack extends Rank {
    override def toString = "J"
    override def value    = 9
  }

  final case object Ten extends Rank {
    override def toString = "T"
    override def value    = 8
  }

  final case object Nine extends Rank {
    override def toString = "9"
    override def value    = 7
  }

  final case object Eight extends Rank {
    override def toString = "8"
    override def value    = 6
  }

  final case object Seven extends Rank {
    override def toString = "7"
    override def value    = 5
  }

  final case object Six extends Rank {
    override def toString = "6"
    override def value    = 4
  }

  final case object Five extends Rank {
    override def toString = "5"
    override def value    = 3
  }

  final case object Four extends Rank {
    override def toString = "4"
    override def value    = 2
  }

  final case object Three extends Rank {
    override def toString = "3"
    override def value    = 1
  }

  final case object Two extends Rank {
    override def toString = "2"
    override def value    = 0
  }

  def apply(input: Char): Option[Rank] =
    input match {
      case 'A' => Some(Ace)
      case 'K' => Some(King)
      case 'Q' => Some(Queen)
      case 'J' => Some(Jack)
      case 'T' => Some(Ten)
      case '9' => Some(Nine)
      case '8' => Some(Eight)
      case '7' => Some(Seven)
      case '6' => Some(Six)
      case '5' => Some(Five)
      case '4' => Some(Four)
      case '3' => Some(Three)
      case '2' => Some(Two)
      case _   => None
    }
}
