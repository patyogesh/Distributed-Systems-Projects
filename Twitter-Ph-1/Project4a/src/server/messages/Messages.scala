package server.messages

import akka.actor.ActorRef

sealed trait Messages
//#RequestListener messages
case class Timeline() extends Messages
case class Tweets() extends Messages
//#Assign service request to actor
case class ProcessService() extends Messages
//#Timeline messages
case class GetMentionsTimeline() extends Messages
case class GetUserTimeline() extends Messages
case class GetHomeTimeline() extends Messages
//#Tweets messages
case class GetRetweets() extends Messages
case class GetShow() extends Messages
case class GetOembed() extends Messages
case class PostRetweet() extends Messages
case class PostUpdate(tweet: String, favorites: Int) extends Messages
case class PostUpdateWithMedia() extends Messages
case class PostDestroy() extends Messages
//#
case class Request(service: String, endPoint: String, tweet: String, followers: Int)

case class Print() extends Messages

//#Load Monitor Messages
case class MeasureLoad() extends Messages
case class InformLoad() extends Messages
case class PrintLoad(load: Int) extends Messages