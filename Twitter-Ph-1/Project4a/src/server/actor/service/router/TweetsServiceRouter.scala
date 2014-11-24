package server.actor.service.router

import akka.actor.Actor
import akka.actor.Terminated
import akka.actor.Props
import server.actor.service.impl.TweetsService
import akka.routing.Router
import akka.routing.RoundRobinRoutingLogic
import akka.routing.ActorRefRoutee
import server.messages.InformLoad
import server.messages.PrintLoad
import akka.routing.Broadcast
import akka.actor.ActorRef
import server.messages.Request
import common.ServiceRequest
import common.UserProfile
import common.Tweet

class TweetsServiceRouter(count: Int, loadMonitor: ActorRef, userProfilesMap: scala.collection.mutable.Map[String, UserProfile], tweetsMap: scala.collection.mutable.Map[String, Tweet]) extends Actor {

  var load: Int = 0
  var router = {
    val routees = Vector.fill(count) {
      val r = context.actorOf(Props(new TweetsService(loadMonitor, userProfilesMap, tweetsMap)))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case Request(request: ServiceRequest) =>
      router.route(Request(request), sender)
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props(new TweetsService(loadMonitor, userProfilesMap, tweetsMap)))
      context watch r
      router = router.addRoutee(r)
    case _ =>
      println("Unknown message received at Tweets Service Router.")
  }
}