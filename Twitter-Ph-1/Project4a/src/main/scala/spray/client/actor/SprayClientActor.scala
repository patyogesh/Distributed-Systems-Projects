package main.scala.spray.client.actor

import akka.actor.ActorSystem
import akka.actor.Actor
import main.scala.common.Start
import main.scala.common.TweetToServer
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.http._
import spray.can.Http
import HttpMethods._
import spray.httpx.RequestBuilding._
import spray.http._
import HttpMethods._
import spray.http.HttpHeaders._
import spray.http.ContentTypes._

class SprayClientActor(serverAddress: String, followers: Int, tweetsPerDay: Int, offset: Double, name: String, totalClients: Int, timeMultiplier: Double) extends Actor {

  implicit val system = ActorSystem()
  private implicit val timeout: Timeout = 5.seconds

  def receive = {
    case Start =>
      println("starting")

    case TweetToServer =>
      //val servicePath = sprayServerAddress + "/tweet/update" + "/bhavnesh"
      //val server = context.actorSelection(servicePath)
      val uuid = java.util.UUID.randomUUID().toString()
      //server ! new AkkaRequest(uuid, selfPath + name, "PostUpdate", name, "", getRandomText)
      import system.dispatcher // execution context for future transformation below
      for {
        response <- IO(Http).ask(HttpRequest(method = POST, uri = Uri(s"http://$serverAddress/tweet/update/bhavnesh"), entity = """{ "text" : """" + getRandomText + """"}""", headers = List(`Content-Type`(`application/json`)))).mapTo[HttpResponse]
        _ <- IO(Http) ? Http.CloseAll
      } yield {
        println("Returned")
        system.log.info("Request-Level API: received {} response with {} bytes",
          response.status, response.entity.data.length)
        response.header[HttpHeaders.Server].get.products.head
      }
  }

  //Generate random String for tweet text
  def getRandomText(): String = {
    val random = new scala.util.Random
    val length: Int = random.nextInt(120) + 20 //min 20 chars, max 140 chars
    val sb = new StringBuilder
    for (i <- 1 to length) {
      //ASCII value 65 to 90 are printable characters
      sb.append((random.nextInt(25) + 65).toChar)
    }
    sb.toString
  }
}