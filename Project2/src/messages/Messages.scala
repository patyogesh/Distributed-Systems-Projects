package messages

import akka.actor.ActorRef

sealed trait Messages
case class Gossip() extends Messages
case class PushSum(s: BigDecimal, w: BigDecimal) extends Messages
case class UpdateLineNeighbours(prev: ActorRef, next: ActorRef) extends Messages


