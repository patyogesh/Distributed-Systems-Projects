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
import constants.Constants

object Main {
  def main(args: Array[String]) {
    val hostAddress: String = java.net.InetAddress.getLocalHost.getHostAddress()
    val TwitterServerPort = 4030

    val configString = """akka {
  actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }
  remote {
    enabled-transports = ["akka.remote.netty.tcp"]
    netty.tcp {
      hostname = """ + hostAddress + """
      port = """ + TwitterServerPort + """
    }
 }
}"""

    val configuration = ConfigFactory.parseString(configString)
    val cores: Int = Runtime.getRuntime().availableProcessors();
    val coresScaleUp: Int = 2

    val system = ActorSystem("Project4aServer", ConfigFactory.load(configuration))

    val serviceRouterMap: Map[String, ActorRef] = createServiceRouterMap(system, cores)

    val listenerRouter = system.actorOf(Props(new RequestListenerRouter(Map[String, ActorRef]() ++ serviceRouterMap)), name = "ProcessingRouter")

  }

  def createServiceRouterMap(system: ActorSystem, cores: Int) = {
    val serviceRouterMap = Map[String, ActorRef]()

    val timelineServiceRouter = system.actorOf(Props(new TimelineServiceRouter()), name = "TimelineServiceRouter")
    val tweetsServiceRouter = system.actorOf(Props(new TweetsServiceRouter()), name = "TweetsServiceRouter")

    serviceRouterMap + ("timeline" -> timelineServiceRouter)
    serviceRouterMap + ("tweets" -> tweetsServiceRouter)

    serviceRouterMap
  }
}