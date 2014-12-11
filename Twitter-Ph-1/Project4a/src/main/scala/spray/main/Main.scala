package spray.main

import akka.actor.ActorSystem
import akka.io.IO
import spray.can.Http
import akka.actor.actorRef2Scala
import spray.actor.service.router.RequestListenerRouter
import akka.actor.Props
import spray.actor.service.impl.RequestListenerService
import common.Constants

object Main {

  def main(args: Array[String]) {
    val akkaServerIP = args(0)
    val localAddress: String = java.net.InetAddress.getLocalHost.getHostAddress()
    val cores: Int = Runtime.getRuntime().availableProcessors();
    val constants = new Constants()
    
    
    implicit val system = ActorSystem("SprayServer")

    // the handler actor replies to incoming HttpRequests
    //val handler = system.actorOf(Props(new RequestListenerRouter(cores)), name = "handler")
    val handler = system.actorOf(Props(new RequestListenerService(akkaServerIP, localAddress, constants.SERVER_PORT)), name = "RequestListener")

    IO(Http) ! Http.Bind(handler, interface = "localhost", port = 9080)
  }

}