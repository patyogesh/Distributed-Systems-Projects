package main.scala.server.main

import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props
import akka.routing.SmallestMailboxRouter
import main.scala.server.actor.dispatch._
import server.actor.service.impl._
import server.actor.service.router.TimelineServiceRouter
import server.actor.service.router.TweetsServiceRouter
import com.typesafe.config.ConfigFactory
import common.Constants
import main.scala.common.UserProfile
import main.scala.common.Tweet
import main.scala.server.actor.service.router.UserRegistrationRouter
import java.util.concurrent.ConcurrentHashMap
import scala.collection._
import scala.collection.convert.decorateAsScala._

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

    val userProfilesMap: concurrent.Map[String, UserProfile] = new ConcurrentHashMap().asScala
    val tweetsMap: concurrent.Map[String, Tweet] = new ConcurrentHashMap().asScala

    createServiceRouter(system, cores, userProfilesMap, tweetsMap, constants)
  }

  def createServiceRouter(system: ActorSystem, cores: Int, userProfilesMap: scala.collection.mutable.Map[String, UserProfile], tweetsMap: scala.collection.mutable.Map[String, Tweet], constants: Constants) = {
    //Monitors Load on server
    val loadMonitor: ActorRef = createLoadMonitor(system, constants)

    //Registers User account on server for clients
    val userRegistrationRouter = system.actorOf(Props(new UserRegistrationRouter(cores * 2, loadMonitor, userProfilesMap, tweetsMap)), name = "UserRegistrationRouter")
    
    //Tweeter web-services
    val timelineServiceRouter = system.actorOf(Props(new TimelineServiceRouter(cores * 2, loadMonitor, userProfilesMap, tweetsMap)), name = "TimelineServiceRouter")
    val tweetsServiceRouter = system.actorOf(Props(new TweetsServiceRouter(cores * 2, loadMonitor, userProfilesMap, tweetsMap)), name = "TweetsServiceRouter")

  }

  def createLoadMonitor(system: ActorSystem, constants: Constants): ActorRef = {
    val loadMonitor = system.actorOf(Props(new LoadMonitor(constants.UPDATE_TIMEOUT)), name = "LoadMonitor")
    loadMonitor
  }
}