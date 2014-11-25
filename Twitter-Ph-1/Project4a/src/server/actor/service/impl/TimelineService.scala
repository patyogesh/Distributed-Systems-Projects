package server.actor.service.impl

import akka.actor.Actor
import akka.actor.actorRef2Scala
import akka.actor.ActorRef
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import common.ServiceRequest
import common.UserProfile
import common.Tweet
import common.Request
import common.RegisterLoad
import common.InformLoad
import common.ReturnHomeTimeline
import common.ReturnUserTimeline
import common.LoadHomeTimelineResp
import common.LoadUserTimelineResp

class TimelineService(loadMonitor: ActorRef, userProfilesMap: scala.collection.mutable.Map[String, UserProfile], tweetsMap: scala.collection.mutable.Map[String, Tweet]) extends Actor {
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
      loadMonitor ! RegisterLoad(load)
      load = 0
    case _ => println("Unknowk message received in Timeline service.");
  }

  def getMentionsTimeline(requestActorPath: String, endPoint: String, userName: String, tweetuuid: String, tweetText: String) = {

  }

  def getHomeTimeline(requestActorPath: String, endPoint: String, userName: String, tweetuuid: String, tweetText: String): Unit = {
    val userProfile: UserProfile = userProfilesMap.get(userName).get
    val homeTimeline: List[String] = userProfile.homeTimeline
    val tweets: List[Tweet] = List()
    var i = 1
    for (id <- homeTimeline if i <= numberOfTweetsPerRequest) {
      tweets :+ tweetsMap.get(id).get
      i += 1
    }
    load += tweets.length
    val client = context.actorSelection(requestActorPath)
    client ! new LoadHomeTimelineResp(tweets)
  }

  def getUserTimeline(requestActorPath: String, endPoint: String, userName: String, tweetuuid: String, tweetText: String): Unit = {
    val userProfile: UserProfile = userProfilesMap.get(userName).get
    val userTimeline: List[String] = userProfile.userTimeline
    val tweets = List[Tweet]()
    var i = 1
    for (id <- userTimeline if i <= numberOfTweetsPerRequest) {
      tweets :+ tweetsMap.get(id).get
      i += 1
    }
    load += tweets.length
    val client = context.actorSelection(requestActorPath)
    client ! new LoadUserTimelineResp(tweets)
  }
}