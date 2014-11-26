package server.actor.service.router

import akka.actor.Actor
import akka.actor.Props
import server.actor.service.impl.TimelineService
import akka.routing.ActorRefRoutee
import akka.routing.Router
import akka.routing.RoundRobinRoutingLogic
import akka.actor.Terminated
import akka.actor.ActorRef
import main.scala.common.ServiceRequest
import main.scala.common.UserProfile
import main.scala.common.Tweet
import main.scala.common.Request

class TimelineServiceRouter(count: Int, loadMonitor: ActorRef, userProfilesMap: scala.collection.mutable.Map[String, UserProfile], tweetsMap: scala.collection.mutable.Map[String, Tweet]) extends Actor {

  var load: Int = 0
  var router = {
    val routees = Vector.fill(count) {
      val r = context.actorOf(Props(new TimelineService(loadMonitor, userProfilesMap, tweetsMap)))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }
  def receive = {
    case Request(requestActorPath: String, endPoint: String, userName: String, tweetuuid: String, tweetText: String) =>
      router.route(Request(requestActorPath: String, endPoint: String, userName: String, tweetuuid: String, tweetText: String), sender)
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props(new TimelineService(loadMonitor, userProfilesMap, tweetsMap)))
      context watch r
      router = router.addRoutee(r)
    case _ =>
      println("Unknown message received at Timeline service router.")
  }
}