package actors

import akka.actor.Actor
import akka.actor.ActorRef

class GridActor extends Actor {

  var top: ActorRef = null
  var left: ActorRef = null
  var bottom: ActorRef = null
  var right: ActorRef = null
  
  def receive() = {
    case gossip =>
      
    case pushSum =>
      
    case _ => //
  }
}