package server.main

import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props
import akka.routing.SmallestMailboxRouter
//import server.actor.service.router.ServiceRouter
import server.actor.dispatch._
import server.actor.service.impl._
import server.actor.service.router.TimelineServiceRouter
import server.actor.service.router.TweetsServiceRouter

object Main {
  def main(args: Array[String]) {
    val cores: Int = Runtime.getRuntime().availableProcessors();
    val coresScaleUp: Int = 2

    val system = ActorSystem("Project4aServer")
    
    val serviceRouterMap: Map[String, ActorRef] = createServiceRouterMap(system, cores)
    
    val listenerRouter = system.actorOf(Props(new RequestListenerRouter(Map[String, ActorRef]() ++ serviceRouterMap)), name = "ProcessingRouter")

  }
  
  def createServiceRouterMap(system: ActorSystem, cores: Int) = {
    val serviceRouterMap = Map[String, ActorRef]()
    
    val timelineServiceRouter = system.actorOf(Props(new TimelineServiceRouter()), name = "TimelineServiceRouter")
    val tweetsServiceRouter = system.actorOf(Props(new TweetsServiceRouter()), name = "TweetsServiceRouter")
    
    serviceRouterMap + ("timeline" -> timelineServiceRouter)
    serviceRouterMap  + ("tweets" -> tweetsServiceRouter)
  
    serviceRouterMap
  }
}