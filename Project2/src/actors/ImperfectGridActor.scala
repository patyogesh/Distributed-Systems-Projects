package actors

import akka.actor.Actor
import akka.actor.ActorRef

class ImperfectActor extends Actor {

  var top: ActorRef = null
  var left: ActorRef = null
  var bottom: ActorRef = null
  var right: ActorRef = null
  var random: ActorRef = null
  
  def receive() = {
    case gossip =>
      
    case pushSum =>
      
  } 
}