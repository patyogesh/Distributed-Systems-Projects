package main.scala.spray.client.main

import scala.concurrent.Future
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.http._
import spray.can.Http
import HttpMethods._
import akka.actor.Actor
import main.scala.common.Constants

class SprayActor extends Actor {
  private implicit val timeout: Timeout = 5.seconds
  val constants = new Constants()
  val host: String = "172.16.110.167:" + constants.SPRAY_SERVER_PORT_FOR_HTTP_MESSAGES 
  implicit val system: ActorSystem = ActorSystem()
  import system.dispatcher // execution context for future transformation below
  for {
    response <- IO(Http).ask(HttpRequest(POST, Uri(s"http://$host/ping"))).mapTo[HttpResponse]
    _ <- IO(Http) ? Http.CloseAll
  } yield {
    system.log.info("Request-Level API: received {} response with {} bytes",
      response.status, response.entity.data.length)
    response.header[HttpHeaders.Server].get.products.head
  }

  
  def receive = {
    case _ =>
      println("Unknown")
  }
}