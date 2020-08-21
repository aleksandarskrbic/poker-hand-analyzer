package poker.repository

import zio._

import poker.model.EqClass

package object lookup {
  type LookupTable = Has[LookupTable.Service]

  def getRank(eqClass: EqClass): URIO[LookupTable, Int] =
    ZIO.accessM(_.get.getRank(eqClass))
}
