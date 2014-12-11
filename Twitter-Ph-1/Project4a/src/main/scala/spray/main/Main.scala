package spray.main

import akka.actor.ActorSystem
import akka.io.IO
import spray.can.Http
import akka.actor.actorRef2Scala
import spray.actor.service.router.RequestListenerRouter
import akka.actor.Props
import spray.actor.service.impl.RequestListenerService

object Main {

  def main(args: Array[String]) {
    val cores: Int = Runtime.getRuntime().availableProcessors();

    implicit val system = ActorSystem()

    // the handler actor replies to incoming HttpRequests
    //val handler = system.actorOf(Props(new RequestListenerRouter(cores)), name = "handler")
    val handler = system.actorOf(Props[RequestListenerService], name = "handler")

    IO(Http) ! Http.Bind(handler, interface = "localhost", port = 9080)
  }

}