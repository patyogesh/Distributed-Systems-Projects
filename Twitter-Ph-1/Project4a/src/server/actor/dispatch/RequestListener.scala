package server.actor.dispatch

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.routing.SmallestMailboxRouter
import server.messages._

class RequestListener(serviceRouterMap: Map[String, ActorRef]) extends Actor {

  def receive = {
    case Timeline =>
      //serviceRouterMap get("timeline") 
    case Tweets =>
      
    case _ => println("Unknown message received.")
  }
  
}