package client.actor

import akka.actor.Actor
import akka.actor.ActorSelection
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import common.ServiceRequest
import common.Constants
import common.TweetToServer
import common.Request
import common.LoadHomeTimeline
import common.LoadHomeTimelineReq
import common.LoadHomeTimelineResp
import common.LoadUserTimelineResp
import common.LoadUserTimelineReq
import common.Tweet
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map
import common.Start

class ClientActor(serverAddress: String, followers: Int, tweetsPerDay: Int, offset: Double, name: String, totalClients: Int, timeMultiplier: Double) extends Actor {

  val constants = new Constants()
  val localAddress: String = java.net.InetAddress.getLocalHost.getHostAddress()
  val server = context.actorSelection(serverAddress + "/UserRegistrationRouter")
  val selfPath = "akka.tcp://Project4aClient@" + localAddress + ":" + constants.SERVER_PORT + "/user/"

  import context.dispatcher

  def receive = {
    case Start =>
      val tweetTimeout = ((24 * 3600) / (tweetsPerDay * timeMultiplier))
      val tweet = context.system.scheduler.schedule((offset / tweetsPerDay) milliseconds, tweetTimeout * 1000 milliseconds, self, TweetToServer)
      val homeTimelineTimeout = ((24 * 3600) / (4 * timeMultiplier))
      val homeTimeline = context.system.scheduler.schedule((offset / 4) milliseconds, homeTimelineTimeout * 1000 milliseconds, self, LoadHomeTimelineReq)
      val userTimelineTimeout = ((24 * 3600) / (1 * timeMultiplier))
      val userTimeline = context.system.scheduler.schedule((offset / 1) milliseconds, userTimelineTimeout * 1000 milliseconds, self, LoadUserTimelineReq)
    case TweetToServer =>
      val servicePath = serverAddress + "/TweetsServiceRouter"
      val server = context.actorSelection(servicePath)
      server ! new Request(selfPath + name, "PostUpdate", name, "", "blah!")
    case LoadHomeTimelineReq =>
      val servicePath = serverAddress + "/TimelineServiceRouter"
      val server = context.actorSelection(servicePath)
      server ! new Request(selfPath + name, "GetHomeTimeline", name, "", "")
    case LoadHomeTimelineResp(tweets: Map[String, String]) =>
    //Trash Received tweets from server 
    case LoadUserTimelineReq =>
      val servicePath = serverAddress + "/TimelineServiceRouter"
      val server = context.actorSelection(servicePath)
      server ! new Request(selfPath + name, "GetUserTimeline", name, "", "")
    case LoadUserTimelineResp(tweets: Map[String, String]) =>
    //Trash Received tweets from server
    case _ =>
      println("Unknown Message received at client")
  }
}