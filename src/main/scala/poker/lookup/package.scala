package poker

import java.nio.file.{ Path, Paths }

import poker.lookup.Lookup.LiveLookupTable
import poker.model.EqClass
import zio.blocking.Blocking
import zio.stream.{ ZStream, ZTransducer }
import zio.{ Has, RIO, Task, UIO, URIO, ZIO, ZLayer }

package object lookup {
  type LookupTable = Has[LookupTable.Service]

  object LookupTable {
    trait Service {
      def getRank(eqClass: EqClass): UIO[Option[Int]]
    }

    def getRank(eqClass: EqClass): URIO[LookupTable, Option[Int]] =
      ZIO.accessM(_.get.getRank(eqClass))

    def live(path: String): ZLayer[Blocking, Throwable, Has[LiveLookupTable]] =
      ZLayer.fromEffect(loadTable(Paths.get(path)).map(new LiveLookupTable(_)))

    private def loadTable(path: Path): RIO[Blocking, Map[EqClass, Int]] =
      ZStream
        .fromFile(path)
        .aggregate(ZTransducer.utfDecode >>> ZTransducer.splitLines)
        .map(cleanLine)
        .mapM(processLine)
        .runCollect
        .map(_.toMap)

    private def cleanLine(line: String): String =
      line.trim.replaceAll(" +", " ")

    private def processLine(line: String): Task[(EqClass, Int)] =
      Task.effect {
        val row = line.split(" ")
        EqClass.make(row) -> row(0).toInt
      }
  }
}
