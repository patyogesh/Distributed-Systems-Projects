package main.scala.spray.server.main

import akka.actor.ActorSystem
import akka.io.IO
import spray.can.Http
import akka.actor.actorRef2Scala
import akka.actor.Props
import main.scala.spray.server.actor.service.impl.RequestListenerService
import main.scala.common.Constants
import akka.actor.ActorRef
import java.util.concurrent.ConcurrentHashMap
import scala.collection._
import scala.collection.convert.decorateAsScala._
import com.typesafe.config.ConfigFactory

object Main {

  def main(args: Array[String]) {
    val akkaServerIP = args(0)
    val localAddress: String = java.net.InetAddress.getLocalHost.getHostAddress()
    val constants = new Constants()
    
    val cores: Int = Runtime.getRuntime().availableProcessors();

    val requestMap: concurrent.Map[String, ActorRef] = new ConcurrentHashMap().asScala

    val configString = """akka {
  actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }
  remote {
    enabled-transports = ["akka.remote.netty.tcp"]
    netty.tcp {
      hostname = """ + localAddress + """
      port = """ + constants.SPRAY_SERVER_PORT_FOR_AKKA_MESSAGES  + """
    }
 }
}"""
      
    val configuration = ConfigFactory.parseString(configString)
    implicit val system = ActorSystem("SprayServer", ConfigFactory.load(configuration))

    // the handler actor replies to incoming HttpRequests
    var handler: ActorRef = null
    handler = system.actorOf(Props(new RequestListenerService("RequestListener", localAddress, constants.SPRAY_SERVER_PORT_FOR_AKKA_MESSAGES , akkaServerIP, constants.AKKA_SERVER_PORT , requestMap)), name = "RequestListener")
    IO(Http) ! Http.Bind(handler, interface = localAddress, port = constants.SPRAY_SERVER_PORT_FOR_HTTP_MESSAGES )
    
  }
}