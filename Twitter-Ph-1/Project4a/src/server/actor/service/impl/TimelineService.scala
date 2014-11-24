package server.actor.service.impl

import akka.actor.Actor
import server.messages._
import akka.actor.actorRef2Scala
import akka.actor.ActorRef
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import common.ServiceRequest
import common.UserProfile
import common.Tweet

class TimelineService(loadMonitor: ActorRef, userProfilesMap: Map[String, UserProfile], tweetsMap: Map[String, Tweet]) extends Actor {
  import context.dispatcher

  var load: Int = 0
  val updateLoad = context.system.scheduler.schedule(0 milliseconds, 2000 milliseconds, self, InformLoad)

  def receive = {
    case Request(request: ServiceRequest) =>
      if (request.endPoint equalsIgnoreCase ("GetMentionsTimeline"))
        getMentionsTimeline(request)
      else if (request.endPoint equalsIgnoreCase ("GetHomeTimeline"))
        getHomeTimeline(request)
      else if (request.endPoint equalsIgnoreCase ("GetUserTimeline"))
        getUserTimeline(request)
    case InformLoad =>
      loadMonitor ! RegisterLoad(load)
    case _ => println("Unknowk message received");
  }

  def getMentionsTimeline(request: ServiceRequest) = {

  }

  def getHomeTimeline(request: ServiceRequest) = {

  }

  def getUserTimeline(request: ServiceRequest) = {

  }
}