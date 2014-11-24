package server.actor.service.router

import common.Tweet
import common.UserProfile
import akka.actor.Actor
import akka.actor.Props
import akka.routing.ActorRefRoutee
import akka.routing.Router
import akka.routing.RoundRobinRoutingLogic
import server.actor.service.impl.UserRegistrationService
import akka.actor.ActorRef
import akka.actor.Terminated
import server.messages.Request
import common.ServiceRequest
import server.messages.RegisterUser
import server.messages.RegisterUser

class UserRegistrationRouter(count: Int, userProfilesMap: scala.collection.mutable.Map[String, UserProfile], tweetsMap: scala.collection.mutable.Map[String, Tweet]) extends Actor {

  var router = {
    val routees = Vector.fill(count) {
      val r = context.actorOf(Props(new UserRegistrationService(userProfilesMap, tweetsMap)))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case RegisterUser(userName: String) =>
      router.route(RegisterUser, sender)
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props(new UserRegistrationService(userProfilesMap, tweetsMap)))
      context watch r
      router = router.addRoutee(r)
    case _ =>
      println("Unknown Message received.")
  }
}