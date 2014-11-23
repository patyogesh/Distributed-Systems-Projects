package server.actor.service.impl

import akka.actor.Actor
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import akka.actor.ActorRef
import server.messages.MeasureLoad
import server.messages.InformLoad
import server.messages.PrintLoad

class LoadMonitor(routers: Array[ActorRef]) extends Actor {
import context.dispatcher
  
  var count: Int = 0
  var serverLoad: Int = 0
  val tweet = context.system.scheduler.schedule(0 milliseconds, 2000 milliseconds, self, MeasureLoad)
  
  def receive = {
    case MeasureLoad =>
      for(r <- routers)
        r ! InformLoad
    case PrintLoad(load) =>
      serverLoad += load
      count += 1
      if(count == routers.length){
        println(serverLoad)
        count = 0
        serverLoad  = 0
      }
  }
}