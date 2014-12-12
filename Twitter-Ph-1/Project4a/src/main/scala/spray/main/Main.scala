package spray.main

import akka.actor.ActorSystem
import akka.io.IO
import spray.can.Http
import akka.actor.actorRef2Scala
import akka.actor.Props
import spray.actor.service.impl.RequestListenerService
import common.Constants
import akka.actor.ActorRef

object Main {

  def main(args: Array[String]) {
    val akkaServerIP = args(0)
    val localAddress: String = java.net.InetAddress.getLocalHost.getHostAddress()
    val cores: Int = Runtime.getRuntime().availableProcessors();
    val constants = new Constants()

    implicit val system = ActorSystem("SprayServer")

    // the handler actor replies to incoming HttpRequests
    var handler: ActorRef = null
    for (i <- 1 to 2 * cores) {
      handler = system.actorOf(Props(new RequestListenerService("RequestListener" + i, akkaServerIP, localAddress, constants.SERVER_PORT)), name = "RequestListener" + i)
      IO(Http) ! Http.Bind(handler, interface = "localhost", port = 9080)
    }
  }
}