# poker-hand-analyzer

Scala + ZIO application for evalution poker hand strength.
Algorithm that is used to evaluate hand is [Cactus-Kev](http://suffe.cool/poker/evaluator.html).
Main idea of this algorithm is that every hand can be encoded to [Hand Equivalence Class](https://github.com/aleksandarskrbic/poker-hand-analyzer/blob/master/src/main/resources/data/classes.txt) and to use [Equivalence Class Table](https://github.com/aleksandarskrbic/poker-hand-analyzer/blob/master/src/main/resources/data/classes.txt) in order to determine rank of a given hand.

## How to run
0. Install [Scala and SBT](http://www.codebind.com/linux-tutorials/install-scala-sbt-java-ubuntu-18-04-lts-linux/)
1. `git clone git@github.com:aleksandarskrbic/poker-hand-analyzer.git && cd poker-hand-analyzer`
2. To run TexasHoldEm evaluator: `sbt run'
2. To run OmahaHoldEm evaluator: `sbt "run --omaha"'
