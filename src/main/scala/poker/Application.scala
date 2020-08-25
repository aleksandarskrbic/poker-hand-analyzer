package poker

import zio._

import poker.game._
import poker.service.rank._

object Application extends zio.App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val rankService = RankService.live("data/classes.txt").orDie

    args.headOption match {
      case None      => TexasHoldEm.loop().provideCustomLayer(rankService).as(ExitCode.success)
      case Some(arg) =>
        if (arg == "--omaha")
          OmahaHoldEm.loop().provideCustomLayer(rankService).as(ExitCode.success)
        else
          ZIO.succeed(ExitCode.failure)
    }
  }

}
