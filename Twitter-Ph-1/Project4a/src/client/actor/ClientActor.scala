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
import common.LoadHomeTimelineReq
import common.LoadHomeTimelineResp
import common.LoadHomeTimelineReq
import common.LoadUserTimelineResp
import common.LoadUserTimelineReq

class ClientActor(serverAddress: String, followers: Int, tweetsPerDay: Int, offset: Int, name: String, totalClients: Int) extends Actor {

  val constants = new Constants()
  val localAddress: String = java.net.InetAddress.getLocalHost.getHostAddress()
  val server = context.actorSelection(serverAddress + "/UserRegistrationRouter")
  val selfPath = "akka.tcp://Project4aClient@" + localAddress + ":" + constants.SERVER_PORT + "/user/"
  //server ! RegisterUser(name)

  import context.dispatcher
  val tweetTimeout = ((24 * 3600) / tweetsPerDay)
  val tweet = context.system.scheduler.schedule((offset / tweetsPerDay) milliseconds, tweetTimeout * 1000 milliseconds, self, TweetToServer)
  val homeTimelineTimeout = ((24 * 3600) / 4)
  val homeTimeline = context.system.scheduler.schedule((offset / 4) milliseconds, homeTimelineTimeout * 1000 milliseconds, self, LoadHomeTimelineReq)
  val userTimelineTimeout = ((24 * 3600) / 1)
  val userTimeline = context.system.scheduler.schedule((offset / 1) milliseconds, userTimelineTimeout * 1000 milliseconds, self, LoadUserTimelineReq)

  def receive = {
    case TweetToServer =>
      val servicePath = serverAddress + "/TweetsServiceRouter"
      val server = context.actorSelection(servicePath)
      server ! new Request(selfPath + name, "PostUpdate", name, "", "blah!")
    case LoadHomeTimelineReq =>
      //Send GET request to Server for <USER>
      val servicePath = serverAddress + "/TimelineServiceRouter"
      val server = context.actorSelection(servicePath)
      server ! new Request(selfPath + name, "GetHomeTimeline", name, "", "")
    case LoadHomeTimelineResp =>
    //Trash Received tweets from server 
    case LoadUserTimelineReq =>
      val servicePath = serverAddress + "/TimelineServiceRouter"
      val server = context.actorSelection(servicePath)
      server ! new Request(selfPath + name, "GetHomeTimeline", name, "", "")
    case LoadUserTimelineResp =>
    //Trash Received tweets from server
    case _ =>
      println("Unknown Message")
  }
}