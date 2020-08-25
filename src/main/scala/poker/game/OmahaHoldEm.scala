package poker.game

import zio._
import zio.console._

import poker.service.rank._
import poker.model.common._
import poker.model.error._
import poker.model.omaha._

object OmahaHoldEm {

  def loop(): ZIO[Console with RankService, Nothing, Unit] =
    for {
      text <- getStrLn.orDie
      _    <- shouldQuit(text).flatMap {
                case true  => ZIO.succeed(())
                case false =>
                  processInput(text).foldM(
                    error => putStrLn(error.text) *> loop(),
                    input => analyzeInput(input) *> loop()
                  )
              }
    } yield ()

  def analyzeInput(input: OmahaInput): URIO[Console with RankService, Unit] =
    for {
      ranks <- calculateRank(combinationsByHand(input))
      _     <- putStrLn(prettyPrint(sortByRank(ranks)))
    } yield ()

  def combinationsByHand(input: OmahaInput): Map[OmahaHand, List[EqClass]] =
    input.allCombinations.map {
      case (k, v) => k -> v.map(EqClass.make)
    }

  def calculateRank(value: Map[OmahaHand, List[EqClass]]): URIO[RankService, List[(OmahaHand, Int)]] =
    ZIO.collect(value.toList) {
      case (k, v) =>
        ZIO.collect(v)(eqClass => getRank(eqClass).map(rank => k -> rank)).map(_.minBy(_._2))
    }

  def processInput(text: String): IO[InvalidInputError, OmahaInput] =
    ZIO.fromOption(OmahaInput.make(text)).orElseFail(InvalidInputError("Invalid input format!"))

  def sortByRank(output: List[(OmahaHand, Int)]): List[List[String]] =
    output
      .groupBy(_._2)
      .map {
        case (k, v) => k -> v.map(_._1).map(_.toString).sortBy(identity)
      }
      .toList
      .sortBy(_._1)
      .reverse
      .map(_._2)
}
