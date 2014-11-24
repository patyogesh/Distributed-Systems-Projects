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

class LoadMonitor() extends Actor {
  import context.dispatcher

  var serverLoad: Int = 0
  val printLoad = context.system.scheduler.schedule(0 milliseconds, 2000 milliseconds, self, PrintLoad)

  def receive = {
    case RegisterLoad(load) =>
      serverLoad += load
    case PrintLoad =>
      println(serverLoad)
      serverLoad = 0
    case _ =>
      println("Unknown message received in Load Monitor.")
  }
}