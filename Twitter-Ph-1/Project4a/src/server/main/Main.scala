package server.main

import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props
import akka.routing.SmallestMailboxRouter
import server.actor.dispatch._
import server.actor.service.impl._
import server.actor.service.router.TimelineServiceRouter
import server.actor.service.router.TweetsServiceRouter
import com.typesafe.config.ConfigFactory
import common.Constants

object Main {
  def main(args: Array[String]) {
    val constants = new Constants()
    
    val hostAddress: String = java.net.InetAddress.getLocalHost.getHostAddress()

    val configString = """akka {
  actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }
  remote {
    enabled-transports = ["akka.remote.netty.tcp"]
    netty.tcp {
      hostname = """ + hostAddress + """
      port = """ + constants.SERVER_PORT + """
    }
 }
}"""

    val configuration = ConfigFactory.parseString(configString)
    val cores: Int = Runtime.getRuntime().availableProcessors();
    val coresScaleUp: Int = 2

    val system = ActorSystem("Project4aServer", ConfigFactory.load(configuration))

    createServiceRouter(system, cores)
  }

  def createServiceRouter(system: ActorSystem, cores: Int) = {
    val loadMonitor: ActorRef = createLoadMonitor(system)

    val timelineServiceRouter = system.actorOf(Props(new TimelineServiceRouter(cores * 2, loadMonitor)), name = "TimelineServiceRouter")
    val tweetsServiceRouter = system.actorOf(Props(new TweetsServiceRouter(cores * 2, loadMonitor)), name = "TweetsServiceRouter")

    val routers = Array(timelineServiceRouter, tweetsServiceRouter)

  }

  def createLoadMonitor(system: ActorSystem): ActorRef = {
    val loadMonitor = system.actorOf(Props(new LoadMonitor()), name = "LoadMonitor")
    loadMonitor
  }
}