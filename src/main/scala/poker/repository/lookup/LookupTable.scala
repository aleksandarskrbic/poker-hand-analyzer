package poker.repository.lookup

import zio._
import zio.stream._
import zio.blocking._
import java.nio.file.{ Path, Paths }
import poker.model.EqClass

object LookupTable {

  trait Service {
    def getRank(eqClass: EqClass): UIO[Int]
  }

  def live(path: String): ZLayer[Blocking, Throwable, LookupTable] =
    ZLayer.fromEffect {
      for {
        path  <- pathFromString(path)
        table <- loadTable(path)
      } yield new Service {
        override def getRank(eqClass: EqClass) =
          ZIO.succeed(table.getOrElse(eqClass, Int.MaxValue))
      }
    }

  private def pathFromString(path: String): Task[Path] =
    Task.effect(Paths.get(getClass.getClassLoader.getResource(path).getPath))

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
