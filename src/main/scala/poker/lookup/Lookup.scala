package poker.lookup

import zio._

import poker.model.EqClass

object Lookup {

  final class LiveLookupTable(map: Map[EqClass, Int]) extends LookupTable.Service {
    override def getRank(eqClass: EqClass): UIO[Option[Int]] =
      ZIO.succeed(map.get(eqClass))
  }

}
