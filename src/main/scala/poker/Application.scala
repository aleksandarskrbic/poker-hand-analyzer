package poker

import zio._
import zio.console._
import zio.blocking._
import poker.model._
import poker.repository.lookup._

object Application extends zio.App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val lookupTable = LookupTable.live("data/classes.txt").orDie
    program.provideCustomLayer(lookupTable).as(ExitCode.success)
  }

  val program: ZIO[Console with LookupTable with Blocking, Nothing, Unit] =
    for {
      queue    <- Queue.unbounded[Input]
      result   <- loop(queue)
      inputs   <- result.takeAll
      analyzed <- analyzeInputs(inputs)
      sorted   <- sortOutput(analyzed)
      _        <- putStrLn("Output: ")
      _        <- ZIO.foreach_(sorted)(input => putStrLn(prettyPrint(input)))
    } yield ()

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

  def analyzeInputs(inputs: List[Input]): URIO[LookupTable, List[List[(StartHand, Int)]]] =
    ZIO.collect(inputs)(input => calculateRank(combinationsByHand(input)))

  def calculateRank(value: Map[StartHand, List[EqClass]]): URIO[LookupTable, List[(StartHand, Int)]] =
    ZIO.collect(value.toList) {
      case (k, v) =>
        ZIO.collect(v)(eqClass => getRank(eqClass).map(rank => k -> rank)).map(_.minBy(_._2))
    }

  def sortOutput(output: List[List[(StartHand, Int)]]): UIO[List[List[List[String]]]] =
    ZIO.collect(output) { line =>
      for {
        grouped <- ZIO.succeed(line.groupBy(_._2))
        sorted  <- ZIO.succeed(grouped.map {
                     case (k, v) => k -> v.map(_._1).map(_.toString).sortBy(identity)
                   })
      } yield sorted.toList.sortBy(_._1).reverse.map(_._2)
    }

  def prettyPrint(output: List[List[String]]): String =
    output
      .map {
        case h :: Nil => h
        case cards    => cards.mkString(" ", "=", "")
      }
      .mkString(" ")
      .trim

  def combinationsByHand(input: Input): Map[StartHand, List[EqClass]] =
    input.allCombinations.map { case (k, v) => k -> v.map(EqClass.make) }

}
