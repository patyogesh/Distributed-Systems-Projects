package server.actor.dispatch

import akka.actor.Actor
import akka.actor.Props
import akka.routing.ActorRefRoutee
import akka.routing.Router
import akka.routing.RoundRobinRoutingLogic
import akka.actor.Terminated
import akka.actor.ActorRef
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import common.Print

class RequestListenerRouter(serviceRouterMap: Map[String, ActorRef]) extends Actor {

  import context.dispatcher
  val tweet = context.system.scheduler.schedule(0 milliseconds, 1000 milliseconds, self, Print)
  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props(new RequestListener(serviceRouterMap)))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }
  var load: Int = 0
  
  
  def receive = {
    /*case Request(service, endPoint, tweet, followers) =>
      load += followers
      router.route(Request, sender)
    case Print =>
      println("Load : " + load)
      load = 0
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[RequestListener])
      context watch r
      router = router.addRoutee(r)*/
    case _ =>
      println("Unknown message received")
  }
}