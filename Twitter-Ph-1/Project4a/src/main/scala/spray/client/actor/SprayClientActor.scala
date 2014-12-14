package main.scala.spray.client.actor

import akka.actor.ActorSystem
import akka.actor.Actor
import main.scala.common._
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
import scala.concurrent.duration.DurationDouble

class SprayClientActor(serverAddress: String, followers: Int, tweetsPerDay: Int, offset: Double, name: String, totalClients: Int, timeMultiplier: Double) extends Actor {

  import context.dispatcher
  implicit val system = ActorSystem()
  private implicit val timeout: Timeout = 5.seconds

  def receive = {
    case Start =>
      val tweetTimeout = ((24 * 3600) / (tweetsPerDay * timeMultiplier))
      val tweet = context.system.scheduler.schedule((offset / tweetsPerDay) * 1000 milliseconds, tweetTimeout * 1000 milliseconds, self, TweetToServer)
      val homeTimelineTimeout = ((24 * 3600) / (4 * timeMultiplier))
      val homeTimeline = context.system.scheduler.schedule((offset / 4) * 1000 milliseconds, homeTimelineTimeout * 1000 milliseconds, self, LoadHomeTimelineReq)
      val userTimelineTimeout = ((24 * 3600) / (1 * timeMultiplier))
      val userTimeline = context.system.scheduler.schedule((offset / 1) * 1000 milliseconds, userTimelineTimeout * 1000 milliseconds, self, LoadUserTimelineReq)

    case TweetToServer =>
      val uuid = java.util.UUID.randomUUID().toString()
      import system.dispatcher // execution context for future transformation below
      for {
        response <- IO(Http).ask(HttpRequest(method = POST, uri = Uri(s"http://$serverAddress/tweet/update/bhavnesh"), entity = """{ "text" : """" + getRandomText + """"}""", headers = List(`Content-Type`(`application/json`)))).mapTo[HttpResponse]
        _ <- IO(Http) ? Http.CloseAll
      } yield {
        //system.log.info("Request-Level API: received {} response with {} bytes",
        // response.status, response.entity.data.length)
        //response.header[HttpHeaders.Server].get.products.head
      }
    case LoadHomeTimelineReq =>
      val uuid = java.util.UUID.randomUUID().toString()
      import system.dispatcher // execution context for future transformation below
      for {
        response <- IO(Http).ask(HttpRequest(method = GET, uri = Uri(s"http://$serverAddress/timeline/hometimeline/bhavnesh"))).mapTo[HttpResponse]
        _ <- IO(Http) ? Http.CloseAll
      } yield {
        //system.log.info("Request-Level API: received {} response with {} bytes",
        // response.status, response.entity.data.length)
        //response.header[HttpHeaders.Server].get.products.head
      }
    case LoadUserTimelineReq =>
      val uuid = java.util.UUID.randomUUID().toString()
      import system.dispatcher // execution context for future transformation below
      for {
        response <- IO(Http).ask(HttpRequest(method = GET, uri = Uri(s"http://$serverAddress/timeline/usertimeline/bhavnesh"))).mapTo[HttpResponse]
        _ <- IO(Http) ? Http.CloseAll
      } yield {
        //system.log.info("Request-Level API: received {} response with {} bytes",
        // response.status, response.entity.data.length)
        //response.header[HttpHeaders.Server].get.products.head
      }
    case _ =>
      println("Unknown message received at spray client actor.")
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