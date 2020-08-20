package poker

import zio._
import zio.stream._
import zio.console._
import zio.blocking._
import java.nio.file.{ Path, Paths }

import poker.model._

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
      _    <- putStrLn("Enter board and hands [Press A if you want to analyze hands]:")
      text <- getStrLn.orDie
      _    <- shouldAnalyze(text).flatMap {
             case true  => ZIO.succeed(queue)
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

  def analyzeInputs(
    inputs: List[Input],
    map: Map[EqClass, Int]
  ): UIO[List[List[(StartHand, Int)]]] =
    ZIO.collect(inputs)(input => calculateRank(combinationsByHand(input), map))

  def calculateRank(
    value: Map[StartHand, List[EqClass]],
    map: Map[EqClass, Int]
  ): UIO[List[(StartHand, Int)]] =
    ZIO.collect(value.toList) {
      case (k, v) =>
        ZIO.collect(v)(eqClass => ZIO.succeed(k -> map(eqClass))).map(_.minBy(_._2))
    }

  def combinationsByHand(input: Input): Map[StartHand, List[EqClass]] =
    input.allCombinations.map { case (k, v) => k -> v.map(EqClass.make) }

  def sortOutput(
    output: List[List[(StartHand, Int)]]
  ): UIO[List[List[(Int, List[String])]]]                             =
    ZIO.collect(output) { line =>
      for {
        grouped <- ZIO.succeed(line.groupBy(_._2))
        sorted  <- ZIO.succeed(grouped.map {
                    case (k, v) => k -> v.map(_._1).map(_.toString).sortBy(identity)
                  })
      } yield sorted.toList.sortBy(_._1).reverse
    }

  def prettyPrint(output: List[(Int, List[String])]): String =
    output.map {
      case (_, h :: Nil) => h
      case (_, cards)    => cards.mkString(" ", "=", "")
      case _             => ""
    }.mkString(" ").trim

  val program: ZIO[Blocking with Console, Nothing, Unit] =
    for {
      path     <- ZIO.succeed(getClass.getClassLoader.getResource("data/classes.txt").getPath)
      map      <- loadTable(Paths.get(path)).orDie
      queue    <- Queue.unbounded[Input]
      result   <- loop(queue)
      inputs   <- result.takeAll
      analyzed <- analyzeInputs(inputs, map)
      sorted   <- sortOutput(analyzed)
      _        <- putStrLn("Output: ")
      _        <- ZIO.foreach_(sorted)(input => putStrLn(prettyPrint(input)))
    } yield ()

  override def run(args: List[String]) =
    program.as(ExitCode.success)
}
