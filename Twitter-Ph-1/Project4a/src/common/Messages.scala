package common

import akka.actor.ActorRef
import common.ServiceRequest
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

sealed trait Messages

case class Request(requestActorPath: String, endPoint: String, val userName: String, val tweetuuid: String, tweetText: String) extends Messages
//#RequestListener messages
case class Timeline() extends Messages
case class Tweets() extends Messages
//#Assign service request to actor
case class ProcessService() extends Messages
//#Timeline messages
case class GetMentionsTimeline() extends Messages
case class ReturnMentionsTimeline(tweets: List[Tweet]) extends Messages
case class GetUserTimeline() extends Messages
case class ReturnUserTimeline(tweets: List[Tweet]) extends Messages
case class GetHomeTimeline() extends Messages
case class ReturnHomeTimeline(tweets: List[Tweet]) extends Messages
//#Tweets messages
case class GetRetweets() extends Messages
case class GetShow() extends Messages
case class GetOembed() extends Messages
case class PostRetweet() extends Messages
case class PostUpdate(tweet: String, favorites: Int) extends Messages
case class PostUpdateWithMedia() extends Messages
case class PostDestroy() extends Messages
//#
//case class Request(service: String, endPoint: String, tweet: String, followers: Int)

case class Print() extends Messages

//#Load Monitor Messages
case class MeasureLoad() extends Messages
case class InformLoad() extends Messages
case class RegisterLoad(load: Int) extends Messages
case class RegisterTweetLoad(load: Int) extends Messages
case class RegisterTimelineLoad(load: Int) extends Messages
case class PrintLoad() extends Messages
case class RegisterService(service: ActorRef) extends Messages
case class UserCount(count: Int) extends Messages
//#User Registration Messages
case class RegisterUser(userName: String) extends Messages
case class RegisterUsers(ip: String, clients: Int, clientFactoryPath: String, followers: Array[Int], sampleSize:Int, peakActorName: String, peakActorFollowersCount: Int) extends Messages
case class UpdateRegisteredUserCount() extends Messages


//#Client Messages
case class TweetToServer() extends Messages
case class LoadHomeTimeline() extends Messages
case class LoadHomeTimelineReq() extends Messages
case class LoadHomeTimelineResp(tweets: Map[String, String]) extends Messages
case class LoadUserTimelineReq() extends Messages
case class LoadUserTimelineResp(tweets: Map[String, String]) extends Messages
case class Start() extends Messages