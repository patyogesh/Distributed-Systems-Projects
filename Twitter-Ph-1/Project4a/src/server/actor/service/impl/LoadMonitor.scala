package server.actor.service.impl

import akka.actor.Actor
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import akka.actor.ActorRef
import server.messages.MeasureLoad
import server.messages.InformLoad
import server.messages.PrintLoad
import server.messages.RegisterService
import server.messages.RegisterLoad
import server.messages.UserCount

class LoadMonitor() extends Actor {
  import context.dispatcher

  var serverLoad: Int = 0
  var userCount: Int = 0
  val printLoad = context.system.scheduler.schedule(0 milliseconds, 2000 milliseconds, self, PrintLoad)
  //val usersRegistered = context.system.scheduler.schedule(0 milliseconds, 2000 milliseconds, self, PrintUserRegisteredCount)

  def receive = {
    case RegisterLoad(load) =>
      serverLoad += load
    case PrintLoad =>
      println("Server Load : " + serverLoad + " . Users Registered : " + userCount )
      serverLoad = 0
    case UserCount(count: Int) =>
      userCount += count
    case _ =>
      println("Unknown message received in Load Monitor.")
  }
}