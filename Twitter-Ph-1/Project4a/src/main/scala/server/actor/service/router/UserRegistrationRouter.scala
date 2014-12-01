package main.scala.server.actor.service.router

import main.scala.common.Tweet
import main.scala.common.UserProfile
import akka.actor.Actor
import akka.actor.Props
import akka.routing.ActorRefRoutee
import akka.routing.Router
import akka.routing.RoundRobinRoutingLogic
import main.scala.server.actor.service.impl.UserRegistrationService
import akka.actor.ActorRef
import akka.actor.Terminated
import main.scala.common.ServiceRequest
import main.scala.common.RegisterUser
import main.scala.common.RegisterUsers

//#Accepts User registration from multiple clients and delegates request to multiple service actors to register the users to server.
class UserRegistrationRouter(count: Int, loadMonitor: ActorRef, userProfilesMap: scala.collection.mutable.Map[String, UserProfile], tweetsMap: scala.collection.mutable.Map[String, Tweet]) extends Actor {

  var router = {
    val routees = Vector.fill(count) {
      val r = context.actorOf(Props(new UserRegistrationService(count, loadMonitor, userProfilesMap, tweetsMap)))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case RegisterUser(userName: String) =>
      router.route(RegisterUser(userName), sender)
    case RegisterUsers(ip: String, clients: Int, clientFactoryPath: String, followers: Array[Int], sampleSize: Int, peakActorName: String, peakActorFollowersCount: Int) =>
      router.route(RegisterUsers(ip, clients, clientFactoryPath, followers, sampleSize, peakActorName, peakActorFollowersCount), sender)
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props(new UserRegistrationService(count, loadMonitor, userProfilesMap, tweetsMap)))
      context watch r
      router = router.addRoutee(r)
    case _ =>
      println("Unknown Message received in User registration router.")
  }
}