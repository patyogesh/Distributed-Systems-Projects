package server.actor.service.impl

import akka.actor.Actor
import server.messages._
import akka.actor.actorRef2Scala
import akka.actor.ActorRef
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import common.ServiceRequest
import common.UserProfile
import common.Tweet

class TimelineService(loadMonitor: ActorRef, userProfilesMap: scala.collection.mutable.Map[String, UserProfile], tweetsMap: scala.collection.mutable.Map[String, Tweet]) extends Actor {
  import context.dispatcher

  var load: Int = 0
  val updateLoad = context.system.scheduler.schedule(0 milliseconds, 2000 milliseconds, self, InformLoad)

  def receive = {
    case Request(requestActorPath: String, endPoint: String, userName: String, tweetuuid: String, tweetText: String) =>
      if (endPoint equalsIgnoreCase ("GetMentionsTimeline"))
        getMentionsTimeline(endPoint, userName, tweetuuid, tweetText)
      else if (endPoint equalsIgnoreCase ("GetHomeTimeline"))
        getHomeTimeline(endPoint, userName, tweetuuid, tweetText)
      else if (endPoint equalsIgnoreCase ("GetUserTimeline"))
        getUserTimeline(endPoint, userName, tweetuuid, tweetText)
    case InformLoad =>
      loadMonitor ! RegisterLoad(load)
    case _ => println("Unknowk message received in Timeline service.");
  }

  def getMentionsTimeline(endPoint: String, userName: String, tweetuuid: String, tweetText: String) = {
	
  }

  def getHomeTimeline(endPoint: String, userName: String, tweetuuid: String, tweetText: String): List[Tweet] = {
    val userProfile: UserProfile = userProfilesMap.get(userName).get
    val homeTimeline: List[String] = userProfile.homeTimeline 
	val tweets: List[Tweet] = List()
    for(id <- homeTimeline){
      tweets :+ tweetsMap.get(id).get
    }
	tweets
  }

  def getUserTimeline(endPoint: String, userName: String, tweetuuid: String, tweetText: String): List[Tweet] = {
	val userProfile: UserProfile = userProfilesMap.get(userName).get
    val userTimeline: List[String] = userProfile.userTimeline 
	val tweets = List[Tweet]()
    for(id <- userTimeline){
      tweets :+ tweetsMap.get(id).get
    }
	tweets
  }
}