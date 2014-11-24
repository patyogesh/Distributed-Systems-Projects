package client.actor

import akka.actor.Actor
import akka.actor.ActorSelection
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import test.Hello
import common.ServiceRequest
import common.Constants
import common.TweetToServer
import common.Request
import common.LoadHomeTimeline
import common.LoadUserTimeline


class ClientActor(serverAddress: String, followers: Int, tweetsPerDay: Int, offset: Int, name: String) extends Actor {

  val constants = new Constants()
  val localAddress: String = java.net.InetAddress.getLocalHost.getHostAddress()
  val server = context.actorSelection(serverAddress + "/UserRegistrationRouter")
  val selfPath = "akka.tcp://Project4aClient@" + localAddress + ":" + constants.SERVER_PORT + "/user/"
  //server ! RegisterUser(name)

  
  import context.dispatcher
  val tweetTimeout = (24 * 3600 / tweetsPerDay) + offset
  val tweet = context.system.scheduler.schedule(0 milliseconds, tweetTimeout * 1000 milliseconds, self, TweetToServer)
  val timelines = context.system.scheduler.schedule(0 milliseconds, tweetTimeout * 1000 milliseconds, self, LoadHomeTimeline)
  
  def receive = {
    case TweetToServer =>
      val servicePath = serverAddress + "/TweetsServiceRouter"
      val server = context.actorSelection(servicePath)
      server ! new Request(selfPath + name, "PostUpdate", name, "", "blah!")
    case LoadHomeTimeline =>
      
      
    case LoadUserTimeline =>
      
    case _ =>
      println("Unknown Message")
  }
}