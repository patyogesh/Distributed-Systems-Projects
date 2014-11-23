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

    createServiceRouter(system, cores)
  }

  def createServiceRouter(system: ActorSystem, cores: Int) = {
    val timelineServiceRouter = system.actorOf(Props(new TimelineServiceRouter(cores*2)), name = "TimelineServiceRouter")
    println(timelineServiceRouter.path)
    val tweetsServiceRouter = system.actorOf(Props(new TweetsServiceRouter(cores*2)), name = "TweetsServiceRouter")
    println(tweetsServiceRouter.path)
  }
}