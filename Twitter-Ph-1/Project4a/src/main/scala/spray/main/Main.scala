package spray.main

import akka.actor.ActorSystem
import akka.io.IO
import spray.can.Http
import akka.actor.actorRef2Scala
import akka.actor.Props
import spray.actor.service.impl.RequestListenerService
import common.Constants
import akka.actor.ActorRef
import java.util.concurrent.ConcurrentHashMap
import scala.collection._
import scala.collection.convert.decorateAsScala._

object Main {

  def main(args: Array[String]) {
    val akkaServerIP = args(0)
    val localAddress: String = java.net.InetAddress.getLocalHost.getHostAddress()
    val constants = new Constants()
    val port = constants.SPRAY_SERVER_PORT 
    val cores: Int = Runtime.getRuntime().availableProcessors();
    
    
    val requestMap: concurrent.Map[String, ActorRef] = new ConcurrentHashMap().asScala
    
    implicit val system = ActorSystem("SprayServer")

    // the handler actor replies to incoming HttpRequests
    var handler: ActorRef = null
    for (i <- 1 to 2 * cores) {
      handler = system.actorOf(Props(new RequestListenerService("RequestListener" + i, akkaServerIP, localAddress, port, requestMap)), name = "RequestListener" + i)
      IO(Http) ! Http.Bind(handler, interface = localAddress, port = port)
    }
  }
}