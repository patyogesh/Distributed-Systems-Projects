package main.scala.spray.actor.service.router

import akka.actor.Actor
import akka.actor.Props
import akka.routing.ActorRefRoutee
import akka.routing.RoundRobinRoutingLogic
import akka.routing.Router
import akka.actor.Terminated
import spray.util._
import spray.http._
import spray.can.Http
import HttpMethods._
import akka.actor.ActorRef
import main.scala.spray.server.actor.service.impl.RequestListenerService
import scala.collection.mutable.Map

class RequestListenerRouter(count: Int, name: String, localAddress: String, localAkkaMessagePort: Int, akkaServerAddress: String, akkaServerPort: Int, followers: Array[Int], requestMap: Map[String, ActorRef]) extends Actor {

  var router = {
    val routees = Vector.fill(count) {
      val r = context.actorOf(Props(new RequestListenerService(name, localAddress, localAkkaMessagePort, akkaServerAddress, akkaServerPort, followers, requestMap)))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case HttpRequest(method, uri, header, entity, protocol) =>
      println("In router")
      router.route(HttpRequest(method, uri, header, entity, protocol), sender)
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props(new RequestListenerService(name, localAddress, localAkkaMessagePort, akkaServerAddress, akkaServerPort, followers, requestMap)))
      context watch r
      router = router.addRoutee(r)
    case a =>
      println("Unknown " + a.toString)
  }
}