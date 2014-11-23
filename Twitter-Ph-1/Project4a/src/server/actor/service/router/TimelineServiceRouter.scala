package server.actor.service.router

import akka.actor.Actor
import akka.actor.Props
import server.actor.service.impl.TimelineService
import akka.routing.ActorRefRoutee
import akka.routing.Router
import akka.routing.RoundRobinRoutingLogic
import akka.actor.Terminated

class TimelineServiceRouter(count: Int) extends Actor {

  var router = {
    val routees = Vector.fill(count) {
      val r = context.actorOf(Props[TimelineService])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }
  def receive = {
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[TimelineService])
      context watch r
      router = router.addRoutee(r)
    case _ =>
      println("Unknown message received")
  }
}