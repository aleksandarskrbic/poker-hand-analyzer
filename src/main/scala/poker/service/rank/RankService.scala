package poker.service.rank

import zio._
import zio.stream._
import zio.blocking._
import java.nio.file.{ Path, Paths }

import poker.model.common._

object RankService {

  trait Service {
    def getRank(eqClass: EqClass): UIO[Int]
  }

  def live(currentDir: String, path: String): ZLayer[Blocking, Throwable, RankService] =
    ZLayer.fromEffect {
      for {
        path  <- pathFromString(currentDir, path)
        table <- loadTable(path)
      } yield new Service {
        override def getRank(eqClass: EqClass) =
          ZIO.succeed(table.getOrElse(eqClass, Int.MaxValue))
      }
    }

  private def pathFromString(currentDir: String, path: String): UIO[Path] =
    ZIO.succeed(Paths.get(currentDir + path))

  private def loadTable(path: Path): RIO[Blocking, Map[EqClass, Int]] =
    ZStream
      .fromFile(path)
      .aggregate(ZTransducer.utfDecode >>> ZTransducer.splitLines)
      .map(cleanLine)
      .mapM(processLine)
      .runCollect
      .map(_.sortBy(_._2))
      .map(_.toMap)

  private def cleanLine(line: String): String =
    line.trim.replaceAll(" +", " ")

  private def processLine(line: String): Task[(EqClass, Int)] =
    Task.effect {
      val row = line.split(" ")
      EqClass.make(row) -> row(0).toInt
    }
}
