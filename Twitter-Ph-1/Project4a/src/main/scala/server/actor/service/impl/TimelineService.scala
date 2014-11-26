package server.actor.service.impl

import akka.actor.Actor
import akka.actor.actorRef2Scala
import akka.actor.ActorRef
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import main.scala.common.ServiceRequest
import main.scala.common.UserProfile
import main.scala.common.Tweet
import main.scala.common.Request
import main.scala.common.RegisterLoad
import main.scala.common.InformLoad
import main.scala.common.ReturnHomeTimeline
import main.scala.common.ReturnUserTimeline
import main.scala.common.LoadHomeTimelineResp
import main.scala.common.LoadUserTimelineResp
import scala.collection.mutable.ListBuffer
import main.scala.common.RegisterTimelineLoad
import scala.collection.mutable.Map

class TimelineService(loadMonitor: ActorRef, userProfilesMap: Map[String, UserProfile], tweetsMap: Map[String, Tweet]) extends Actor {
  import context.dispatcher

  var load: Int = 0
  val updateLoad = context.system.scheduler.schedule(0 milliseconds, 2000 milliseconds, self, InformLoad)

  val numberOfTweetsPerRequest = 20

  def receive = {
    case Request(requestActorPath: String, endPoint: String, userName: String, tweetuuid: String, tweetText: String) =>
      if (endPoint equalsIgnoreCase ("GetMentionsTimeline"))
        getMentionsTimeline(requestActorPath, endPoint, userName, tweetuuid, tweetText)
      else if (endPoint equalsIgnoreCase ("GetHomeTimeline"))
        getHomeTimeline(requestActorPath, endPoint, userName, tweetuuid, tweetText)
      else if (endPoint equalsIgnoreCase ("GetUserTimeline"))
        getUserTimeline(requestActorPath, endPoint, userName, tweetuuid, tweetText)
    case InformLoad =>
      loadMonitor ! RegisterTimelineLoad(load)
      load = 0
    case _ => println("Unknowk message received in Timeline service.");
  }

  def getMentionsTimeline(requestActorPath: String, endPoint: String, userName: String, tweetuuid: String, tweetText: String) = {

  }

  def getHomeTimeline(requestActorPath: String, endPoint: String, userName: String, tweetuuid: String, tweetText: String): Unit = {
    val userProfile: UserProfile = userProfilesMap.get(userName).get
    val homeTimeline: ListBuffer[String] = userProfile.homeTimeline
    val tweets = Map[String, String]()
    var i = 1
    for (id <- homeTimeline if i <= numberOfTweetsPerRequest) {
      tweets += id -> tweetsMap.get(id).get.text
      i += 1
    }
    load += tweets.size
    val client = context.actorSelection(requestActorPath)
    client ! new LoadHomeTimelineResp(tweets)
  }

  def getUserTimeline(requestActorPath: String, endPoint: String, userName: String, tweetuuid: String, tweetText: String): Unit = {
    try {
      val userProfile: UserProfile = userProfilesMap.get(userName).get
      val userTimeline: ListBuffer[String] = userProfile.userTimeline
      val tweets = Map[String, String]()
      var i = 1
      for (id <- userTimeline if i <= numberOfTweetsPerRequest) {
        tweets += id -> tweetsMap.get(id).get.text
        i += 1
      }
      load += tweets.size
      val client = context.actorSelection(requestActorPath)
      client ! new LoadUserTimelineResp(tweets)
    } catch {
      case e: java.util.NoSuchElementException => //Ignore for Unregistered User println("Username : " + userName)
    }
  }
}