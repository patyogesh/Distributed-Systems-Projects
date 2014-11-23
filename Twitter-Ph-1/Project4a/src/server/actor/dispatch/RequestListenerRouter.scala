package server.actor.dispatch

import akka.actor.Actor
import akka.actor.Props
import akka.routing.ActorRefRoutee
import akka.routing.Router
import akka.routing.RoundRobinRoutingLogic
import akka.actor.Terminated
import akka.actor.ActorRef

class RequestListenerRouter(serviceRouterMap: Map[String, ActorRef]) extends Actor {

  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props(new RequestListener(serviceRouterMap)))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }
  def receive = {
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[RequestListener])
      context watch r
      router = router.addRoutee(r)
    case _ =>
      println("Unknown message received")
  }
}