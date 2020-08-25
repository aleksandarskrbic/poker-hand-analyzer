package poker.service

import zio._
import poker.model.common._

package object rank {
  type RankService = Has[RankService.Service]

  def getRank(eqClass: EqClass): URIO[RankService, Int] =
    ZIO.accessM(_.get.getRank(eqClass))
}
