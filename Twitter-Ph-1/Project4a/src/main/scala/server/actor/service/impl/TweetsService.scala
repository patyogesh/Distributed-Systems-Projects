package server.actor.service.impl

import akka.actor.Actor
import akka.actor.actorRef2Scala
import akka.actor.ActorRef
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import main.scala.common.ServiceRequest
import main.scala.common.Tweet
import main.scala.common.UserProfile
import java.lang.Class
import main.scala.common.AkkaRequest
import main.scala.common.InformLoad
import scala.collection.mutable.ListBuffer
import main.scala.common.RegisterTweetLoad
import scala.collection.mutable.Map

//#This services any tweet request coming form user.
class TweetsService(loadMonitor: ActorRef, userProfilesMap: Map[String, UserProfile], tweetsMap: Map[String, Tweet]) extends Actor {
  import context.dispatcher

  var load: Int = 0
  val updateLoad = context.system.scheduler.schedule(0 milliseconds, 2000 milliseconds, self, InformLoad)

  def receive = {
    case AkkaRequest(requestActorPath: String, endPoint: String, userName: String, tweetuuid: String, tweetText: String) =>
      if (endPoint equalsIgnoreCase ("GetRetweets"))
        getRetweets(userName, tweetuuid, tweetText)
      else if (endPoint equalsIgnoreCase ("GetShow"))
        getShow(userName, tweetuuid, tweetText)
      else if (endPoint equalsIgnoreCase ("GetOembed"))
        getOembed(userName, tweetuuid, tweetText)
      else if (endPoint equalsIgnoreCase ("PostRetweet"))
        postRetweet(userName, tweetuuid, tweetText)
      else if (endPoint equalsIgnoreCase ("PostUpdate"))
        postUpdate(userName, tweetuuid, tweetText)
      else if (endPoint equalsIgnoreCase ("PostUpdateWithMedia"))
        postUpdateWithMedia(userName, tweetuuid, tweetText)
      else if (endPoint equalsIgnoreCase ("PostDestroy"))
        postDestroy(userName, tweetuuid, tweetText)
      else
        println("Unknown end point called form Tweet Service")
    case InformLoad =>
      loadMonitor ! RegisterTweetLoad(load)
      load = 0
    case _ => println("Unknown message received in Tweets service.")
  }

  def getRetweets(userName: String, tweetuuid: String, tweetText: String) = {

  }

  def getShow(userName: String, tweetuuid: String, tweetText: String) = {

  }

  def getOembed(userName: String, tweetuuid: String, tweetText: String) = {

  }

  def postRetweet(userName: String, tweetuuid: String, tweetText: String) = {
    val uuid: String = tweetuuid
    val userProfile: UserProfile = userProfilesMap.get(userName).get
    //Push to user profile
    uuid +=: userProfile.userTimeline
    //Push to followers
    for (follower <- userProfile.followers) {
      uuid +=: userProfilesMap.get(follower).get.homeTimeline
    }
    //Register Load
    load += userProfile.followers.length + 2
  }

  def postUpdate(userName: String, tweetuuid: String, tweetText: String) = {
    try {
      //Push to tweet map
      var done = false
      var uuid: String = ""
      while (!done) {
        uuid = java.util.UUID.randomUUID().toString()

        if (tweetsMap.get(uuid) == None) {
          tweetsMap += uuid -> new Tweet(uuid, tweetText, userName)
          done = true
        }
      }
      val userProfile: UserProfile = userProfilesMap.get(userName).get
      //Push to user profile
      uuid +=: userProfile.userTimeline
      //Push to followers
      for (follower <- userProfile.followers) {
        uuid +=: userProfilesMap.get(follower).get.homeTimeline
      }
      //Register load
      load += userProfile.followers.length + 2
    } catch {
      case e: java.util.NoSuchElementException => //Ignore for Unregistered User println("Username : " + userName)
    }
  }

  def postUpdateWithMedia(userName: String, tweetuuid: String, tweetText: String) = {

  }

  def postDestroy(userName: String, tweetuuid: String, tweetText: String) = {
    tweetsMap.remove(tweetuuid)
  }
}