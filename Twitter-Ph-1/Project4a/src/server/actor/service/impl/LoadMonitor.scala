package server.actor.service.impl

import akka.actor.Actor
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import akka.actor.ActorRef
import common.PrintLoad
import common.RegisterLoad
import common.UserCount
import common.RegisterTweetLoad
import common.RegisterTimelineLoad

class LoadMonitor() extends Actor {
  import context.dispatcher

  var serverLoad: Int = 0
  var totalLoad: Int = 0
  var userCount: Int = 0
  var serverTweetLoad: Int = 0
  var serverTimelineLoad: Int = 0
  val printLoad = context.system.scheduler.schedule(0 milliseconds, 2000 milliseconds, self, PrintLoad)
  
  def receive = {
    case RegisterLoad(load) =>
      serverLoad += load
      totalLoad += load
    case RegisterTweetLoad(load) =>
      serverTweetLoad += load
      totalLoad += load
    case RegisterTimelineLoad(load) =>
      serverTimelineLoad += load
      totalLoad += load
    case PrintLoad =>
      println("Total Server Load till now : " + totalLoad  + " . Users Registered : " + userCount + " . Server Tweets Load : " + serverTweetLoad + " . Server Timeline Load : " + serverTimelineLoad )
      serverTweetLoad  = 0
      serverTimelineLoad = 0 
    case UserCount(count: Int) =>
      userCount += count
    case _ =>
      println("Unknown message received in Load Monitor.")
  }
}