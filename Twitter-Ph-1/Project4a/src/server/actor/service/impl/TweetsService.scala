package server.actor.service.impl

import akka.actor.Actor
import server.messages._
import akka.actor.actorRef2Scala
import akka.actor.ActorRef
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import common.ServiceRequest
import server.messages.Request
import server.messages.InformLoad
import common.Tweet
import common.UserProfile
import common.UserProfile
import java.lang.Class

class TweetsService(loadMonitor: ActorRef, userProfilesMap: scala.collection.mutable.Map[String, UserProfile], tweetsMap: scala.collection.mutable.Map[String, Tweet]) extends Actor {
  import context.dispatcher

  var load: Int = 0
  val updateLoad = context.system.scheduler.schedule(0 milliseconds, 2000 milliseconds, self, InformLoad)

  def receive = {
    case Request(request: ServiceRequest) =>
      if (request.endPoint equalsIgnoreCase ("GetRetweets"))
        getRetweets(request)
      else if (request.endPoint equalsIgnoreCase ("GetShow"))
        getShow(request)
      else if (request.endPoint equalsIgnoreCase ("GetOembed"))
        getOembed(request)
      else if (request.endPoint equalsIgnoreCase ("PostRetweet"))
        postRetweet(request)
      else if (request.endPoint equalsIgnoreCase ("PostUpdate"))
        postUpdate(request)
      else if (request.endPoint equalsIgnoreCase ("PostUpdateWithMedia"))
        postUpdateWithMedia(request)
      else if (request.endPoint equalsIgnoreCase ("PostDestroy"))
        postDestroy(request)
      else
        println("Unknown end point")
    case InformLoad =>
      loadMonitor ! RegisterLoad(load)
      load = 0
    case _ => println("Unknown message received in Tweets service.")
  }

  def getRetweets(request: ServiceRequest) = {

  }

  def getShow(request: ServiceRequest) = {

  }

  def getOembed(request: ServiceRequest) = {

  }

  def postRetweet(request: ServiceRequest) = {
	val uuid: String = request.tweetuuid
    val userProfile: UserProfile = userProfilesMap.get(request.userName).get
    //Push to user profile
    uuid :: userProfile.userTimeline
    //Push to followers
    for (follower <- userProfile.followers) {
      uuid :: userProfilesMap.get(follower).get.homeTimeline
    }
  }

  def postUpdate(request: ServiceRequest) = {
    //Push to tweet map
    var done = false
    var uuid: String = ""
    while (!done) {
      uuid = java.util.UUID.randomUUID().toString()
      if (tweetsMap.get(uuid) == null) {
    	tweetsMap += uuid -> new Tweet(uuid, request.tweetText)   
      }
    }
    val userProfile: UserProfile = userProfilesMap.get(request.userName).get
    //Push to user profile
    uuid :: userProfile.userTimeline
    //Push to followers
    for (follower <- userProfile.followers) {
      uuid :: userProfilesMap.get(follower).get.homeTimeline
    }
  }

  def postUpdateWithMedia(request: ServiceRequest) = {

  }

  def postDestroy(request: ServiceRequest) = {
	tweetsMap.remove(request.tweetuuid)
  }
}