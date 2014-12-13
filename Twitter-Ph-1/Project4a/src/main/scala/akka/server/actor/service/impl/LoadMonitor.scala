package main.scala.akka.server.actor.service.impl

import akka.actor.Actor
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import akka.actor.ActorRef
import main.scala.common.PrintLoad
import main.scala.common.UserCount
import main.scala.common.RegisterTweetLoad
import main.scala.common.RegisterTimelineLoad

//#This is the load monitor of tweeter server and measures all load on server.
class LoadMonitor(updateTimeout: Int) extends Actor {
  import context.dispatcher

  var serverLoad: Int = 0
  var userCount: Int = 0
  var serverTweetLoad: Int = 0
  var serverTimelineLoad: Int = 0
  val printLoad = context.system.scheduler.schedule(0 milliseconds, updateTimeout milliseconds, self, PrintLoad)

  def receive = {
    case RegisterTweetLoad(load) =>
      serverTweetLoad += load
    case RegisterTimelineLoad(load) =>
      serverTimelineLoad += load
    case PrintLoad =>
      println("Total Server Load : " + (serverTweetLoad + serverTimelineLoad) + " . Server Tweets Load : " + serverTweetLoad + " . Server Timeline Load : " + serverTimelineLoad + " . Users Registered : " + userCount)
      serverTweetLoad = 0
      serverTimelineLoad = 0
    case UserCount(count: Int) =>
      userCount += count
    case _ =>
      println("Unknown message received in Load Monitor.")
  }
}