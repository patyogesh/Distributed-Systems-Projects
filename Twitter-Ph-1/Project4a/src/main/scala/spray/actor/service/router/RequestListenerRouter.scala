package spray.actor.service.router

import akka.actor.Actor
import akka.actor.Props
import spray.actor.service.impl.RequestListenerService
import akka.routing.ActorRefRoutee
import akka.routing.RoundRobinRoutingLogic
import akka.routing.Router
import akka.actor.Terminated
import spray.http.HttpRequest
import spray.http.HttpRequest
import spray.http.HttpResponse

class RequestListenerRouter(count: Int, akkaServerIP: String, localAddress: String, port: Int) extends Actor {

  var router = {
    val routees = Vector.fill(count) {
      val r = context.actorOf(Props(new RequestListenerService(akkaServerIP, localAddress, port)))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }
  def receive = {
    case HttpRequest(method, uri, header, entity, protocol) =>
      router.route(HttpRequest(method, uri, header, entity, protocol), sender)
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props(new RequestListenerService(akkaServerIP, localAddress, port)))
      context watch r
      router = router.addRoutee(r)
  }
}