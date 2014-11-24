package server.actor.service.impl

import akka.actor.Actor
import server.messages._
import akka.actor.actorRef2Scala
import akka.actor.ActorRef
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import common.ServiceRequest
import server.messages.InformLoad
import common.Tweet
import common.UserProfile
import java.lang.Class

class TweetsService(loadMonitor: ActorRef, userProfilesMap: scala.collection.mutable.Map[String, UserProfile], tweetsMap: scala.collection.mutable.Map[String, Tweet]) extends Actor {
  import context.dispatcher

  var load: Int = 0
  val updateLoad = context.system.scheduler.schedule(0 milliseconds, 2000 milliseconds, self, InformLoad)

  def receive = {
    case Request(requestActorPath: String, endPoint: String, userName: String, tweetuuid: String, tweetText: String) =>
      if (endPoint equalsIgnoreCase ("GetRetweets"))
        getRetweets(endPoint, userName, tweetuuid, tweetText)
      else if (endPoint equalsIgnoreCase ("GetShow"))
        getShow(endPoint, userName, tweetuuid, tweetText)
      else if (endPoint equalsIgnoreCase ("GetOembed"))
        getOembed(endPoint, userName, tweetuuid, tweetText)
      else if (endPoint equalsIgnoreCase ("PostRetweet"))
        postRetweet(endPoint, userName, tweetuuid, tweetText)
      else if (endPoint equalsIgnoreCase ("PostUpdate"))
        postUpdate(endPoint, userName, tweetuuid, tweetText)
      else if (endPoint equalsIgnoreCase ("PostUpdateWithMedia"))
        postUpdateWithMedia(endPoint, userName, tweetuuid, tweetText)
      else if (endPoint equalsIgnoreCase ("PostDestroy"))
        postDestroy(endPoint, userName, tweetuuid, tweetText)
      else
        println("Unknown end point")
    case InformLoad =>
      loadMonitor ! RegisterLoad(load)
      load = 0
    case _ => println("Unknown message received in Tweets service.")
  }

  def getRetweets(endPoint: String, userName: String, tweetuuid: String, tweetText: String) = {

  }

  def getShow(endPoint: String, userName: String, tweetuuid: String, tweetText: String) = {

  }

  def getOembed(endPoint: String, userName: String, tweetuuid: String, tweetText: String) = {

  }

  def postRetweet(endPoint: String, userName: String, tweetuuid: String, tweetText: String) = {
	val uuid: String = tweetuuid
    val userProfile: UserProfile = userProfilesMap.get(userName).get
    //Push to user profile
    uuid :: userProfile.userTimeline
    //Push to followers
    for (follower <- userProfile.followers) {
      uuid :: userProfilesMap.get(follower).get.homeTimeline
    }
	//Register Load
	load += userProfile.followers.length + 2
  }

  def postUpdate(endPoint: String, userName: String, tweetuuid: String, tweetText: String) = {
    //Push to tweet map
    
    var done = false
    var uuid: String = ""
    while (!done) {
      uuid = java.util.UUID.randomUUID().toString()
    
      if (tweetsMap.get(uuid) == None) {
    	tweetsMap += uuid -> new Tweet(uuid, tweetText)
    	done = true
      }
    }
    
    val userProfile: UserProfile = userProfilesMap.get(userName).get
    //Push to user profile
    uuid :: userProfile.userTimeline
    //Push to followers
    for (follower <- userProfile.followers) {
      uuid :: userProfilesMap.get(follower).get.homeTimeline
    }
    //Register load
    load += userProfile.followers.length + 2
  }

  def postUpdateWithMedia(endPoint: String, userName: String, tweetuuid: String, tweetText: String) = {

  }

  def postDestroy(endPoint: String, userName: String, tweetuuid: String, tweetText: String) = {
	tweetsMap.remove(tweetuuid)
  }
}