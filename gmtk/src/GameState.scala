package GMTK

import scala.concurrent._

case class GameState(/*var player:Future[Player]*/var ship:Ship,var entities:List[Entity] = Nil,var repairs:List[Repair] = Nil,var ions:List[Ion] = Nil,var timeRemaining:Double = 60 + 30){

   
}

