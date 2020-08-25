package poker

import zio._
import poker.game._
import poker.service.rank._

object Application extends zio.App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val currentDir  = System.getProperty("user.dir") + "/src/main/resources/"
    val rankService = RankService.live(currentDir, "data/classes.txt").orDie

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
