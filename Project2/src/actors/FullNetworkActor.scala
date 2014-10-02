package actors

import akka.actor.Actor
import akka.actor.ActorRef

class FullNetworkActor extends Actor {

  var neighbours: Array[ActorRef] = null
  
  def receive() = {
    case gossip =>
      
    case pushSum =>
      
  }
}