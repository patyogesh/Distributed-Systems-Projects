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
import server.messages.PostUpdate
import server.messages.PostUpdate

class TweetsServiceRouter(count: Int) extends Actor {

  var load: Int = 0
  var router = {
    val routees = Vector.fill(count) {
      val r = context.actorOf(Props[TweetsService])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }
  def receive = {
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[TweetsService])
      context watch r
      router = router.addRoutee(r)
    case InformLoad =>
      sender ! PrintLoad(load) 
      load = 0
    case PostUpdate(tweet, favorite) =>
      load += favorite
      router.route(PostUpdate, sender)
    case w =>
      load += 1
      router.route(w, sender)
    case _ =>
      println("Unknown message received")
  }
}