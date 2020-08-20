package poker.lookup

import java.nio.file.{ Path, Paths }

import poker.model.EqClass
import zio._
import zio.blocking.Blocking
import zio.stream.{ ZStream, ZTransducer }
import zio.{ UIO, ZLayer }

object Lookup {

  final class LiveLookupTable(map: Map[EqClass, Int]) extends LookupTable.Service {
    override def getRank(eqClass: EqClass): UIO[Option[Int]] =
      ZIO.succeed(map.get(eqClass))
  }

}
