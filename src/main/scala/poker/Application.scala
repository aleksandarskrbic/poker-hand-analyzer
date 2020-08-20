package poker

import zio._
import zio.console._
import zio.blocking.Blocking
import zio.stream.{ ZStream, ZTransducer }
import java.nio.file.{ Path, Paths }
import poker.model.{ EqClass, Input, InvalidInputError, StartHand }

object Application extends zio.App {

  def cleanLine(line: String): String =
    line.trim.replaceAll(" +", " ")

  def processLine(line: String): Task[(EqClass, Int)] =
    Task.effect {
      val row     = line.split(" ")
      val eqClass = EqClass.make(row)

      eqClass -> row(0).toInt
    }

  def loadTable(path: Path): RIO[Blocking, Map[EqClass, Int]] =
    ZStream
      .fromFile(path)
      .aggregate(ZTransducer.utfDecode >>> ZTransducer.splitLines)
      .map(cleanLine)
      .mapM(processLine)
      .runCollect
      .map(_.toMap)

  def loop(queue: Queue[Input]): URIO[Console, Queue[Input]] =
    for {
      _ <- putStrLn("Enter board and hands [Press A if you want to analyze hands]:")
      text <- getStrLn.orDie
      _ <- shouldAnalyze(text).flatMap {
            case true => ZIO.succeed(queue)
            case false =>
              processInput(text).foldM(
                error => putStrLn(error.text) *> loop(queue),
                input => queue.offer(input).flatMap(_ => loop(queue))
              )
          }
    } yield queue

  def shouldAnalyze(text: String): UIO[Boolean] =
    ZIO.succeed(text.length == 1 && text.charAt(0).toUpper == 'A')

  def processInput(text: String): IO[InvalidInputError, Input] =
    ZIO.fromOption(Input.make(text)).orElseFail(InvalidInputError("Invalid input format!"))

  def calculateRank(value: Map[StartHand, List[EqClass]], map: Map[EqClass, Int]) =
    ZIO.foreach_(value) {
      case (k, v) =>
        ZIO.foreach_(v)(
          eq =>
            for {
              _ <- putStrLn("eqClass => " + eq)
              rank <- ZIO.succeed(map(eq))
              _ <- putStrLn(k.toString + " => " + rank.toString)
            } yield ()
        )
    }

  def analyzeInput(input: Input, map: Map[EqClass, Int]): ZIO[Console, Nothing, Unit] =
    for {
      _ <- calculateRank(input.allCombinations.map { case (k, v) => k -> v.map(EqClass.make) }, map)
    } yield ()

  val program: ZIO[Blocking with Console, Nothing, Unit] =
    for {
      path <- ZIO.succeed(getClass.getClassLoader.getResource("data/classes.txt").getPath)
      map <- loadTable(Paths.get(path)).orDie
      queue <- Queue.unbounded[Input]
      result <- loop(queue)
      inputs <- result.takeAll
      _ <- ZIO.foreach_(inputs)(input => analyzeInput(input, map))
    } yield ()

  override def run(args: List[String]) =
    program.as(ExitCode.success)

}
