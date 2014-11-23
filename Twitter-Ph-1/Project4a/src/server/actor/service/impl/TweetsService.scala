package server.actor.service.impl

import akka.actor.Actor
import server.messages._
import akka.actor.actorRef2Scala
import akka.actor.ActorRef
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import common.ServiceRequest
import server.messages.Request
import server.messages.InformLoad

class TweetsService(loadMonitor: ActorRef) extends Actor {
  import context.dispatcher

  var load: Int = 0
  val updateLoad = context.system.scheduler.schedule(0 milliseconds, 2000 milliseconds, self, InformLoad)

  def receive = {
    case Request(request: ServiceRequest) =>
      if (request.endPoint equalsIgnoreCase ("GetRetweets"))
        getRetweets(request)
      else if (request.endPoint equalsIgnoreCase ("GetShow"))
        getShow(request)
      else if (request.endPoint equalsIgnoreCase ("GetOembed"))
        getOembed(request)
      else if (request.endPoint equalsIgnoreCase ("PostRetweet"))
        postRetweet(request)
      else if (request.endPoint equalsIgnoreCase ("PostUpdate"))
        postUpdate(request)
      else if (request.endPoint equalsIgnoreCase ("PostUpdateWithMedia"))
        postUpdateWithMedia(request)
      else if (request.endPoint equalsIgnoreCase ("PostDestroy"))
        postDestroy(request)
    case InformLoad =>
      loadMonitor ! RegisterLoad(load)
      load = 0
    case _ => println("Unknown message received")
  }

  def getRetweets(request: ServiceRequest) = {

  }

  def getShow(request: ServiceRequest) = {

  }

  def getOembed(request: ServiceRequest) = {

  }

  def postRetweet(request: ServiceRequest) = {

  }

  def postUpdate(request: ServiceRequest) = {

  }

  def postUpdateWithMedia(request: ServiceRequest) = {

  }

  def postDestroy(request: ServiceRequest) = {

  }
}