package server.actor.service.router

import akka.actor.Actor
import akka.actor.Props
import server.actor.service.impl.TimelineService
import akka.routing.ActorRefRoutee
import akka.routing.Router
import akka.routing.RoundRobinRoutingLogic
import akka.actor.Terminated
import server.messages.InformLoad
import server.messages.PrintLoad
import akka.actor.ActorRef
import server.messages.Request
import common.ServiceRequest
import common.UserProfile
import common.Tweet

class TimelineServiceRouter(count: Int, loadMonitor: ActorRef, userProfilesMap: Map[String, UserProfile], tweetsMap: Map[String, Tweet]) extends Actor {

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
    case Request(request: ServiceRequest) =>
      router.route(request, sender)
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[TimelineService])
      context watch r
      router = router.addRoutee(r)
    case _ =>
      println("Unknown message received")
  }
}