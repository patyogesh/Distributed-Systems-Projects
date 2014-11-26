package main.scala.client.actor

import akka.actor.Actor
import akka.actor.ActorSelection
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import main.scala.common.ServiceRequest
import common.Constants
import main.scala.common.TweetToServer
import main.scala.common.Request
import main.scala.common.LoadHomeTimeline
import main.scala.common.LoadHomeTimelineReq
import main.scala.common.LoadHomeTimelineResp
import main.scala.common.LoadUserTimelineResp
import main.scala.common.LoadUserTimelineReq
import main.scala.common.Tweet
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map
import main.scala.common.Start

class ClientActor(serverAddress: String, followers: Int, tweetsPerDay: Int, offset: Double, name: String, totalClients: Int, timeMultiplier: Double) extends Actor {

  val constants = new Constants()
  val localAddress: String = java.net.InetAddress.getLocalHost.getHostAddress()
  val server = context.actorSelection(serverAddress + "/UserRegistrationRouter")
  val selfPath = "akka.tcp://Project4aClient@" + localAddress + ":" + constants.SERVER_PORT + "/user/"

  import context.dispatcher

  def receive = {
    case Start =>
      val tweetTimeout = ((24 * 3600) / (tweetsPerDay * timeMultiplier))
      val tweet = context.system.scheduler.schedule((offset / tweetsPerDay) * 1000 milliseconds, tweetTimeout * 1000 milliseconds, self, TweetToServer)
      val homeTimelineTimeout = ((24 * 3600) / (4 * timeMultiplier))
      val homeTimeline = context.system.scheduler.schedule((offset / 4) * 1000 milliseconds, homeTimelineTimeout * 1000 milliseconds, self, LoadHomeTimelineReq)
      val userTimelineTimeout = ((24 * 3600) / (1 * timeMultiplier))
      val userTimeline = context.system.scheduler.schedule((offset / 1) * 1000 milliseconds, userTimelineTimeout * 1000 milliseconds, self, LoadUserTimelineReq)
      println("Timeout for Home timeline : " + homeTimelineTimeout + " . Timeout for user timeline : " + userTimelineTimeout)
    case TweetToServer =>
      val servicePath = serverAddress + "/TweetsServiceRouter"
      val server = context.actorSelection(servicePath)
      server ! new Request(selfPath + name, "PostUpdate", name, "", "blah!")
    case LoadHomeTimelineReq =>
      println("HomeTimeLine")
      val servicePath = serverAddress + "/TimelineServiceRouter"
      val server = context.actorSelection(servicePath)
      server ! new Request(selfPath + name, "GetHomeTimeline", name, "", "")
    case LoadHomeTimelineResp(tweets: Map[String, String]) =>
    //Trash Received tweets from server 
    case LoadUserTimelineReq =>
      println("UserTimeLine")
      val servicePath = serverAddress + "/TimelineServiceRouter"
      val server = context.actorSelection(servicePath)
      server ! new Request(selfPath + name, "GetUserTimeline", name, "", "")
    case LoadUserTimelineResp(tweets: Map[String, String]) =>
    //Trash Received tweets from server
    case _ =>
      println("Unknown Message received at client")
  }
}