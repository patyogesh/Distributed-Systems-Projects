package server.actor.service.impl

import akka.actor.Actor
import server.messages._
import akka.actor.actorRef2Scala

class TweetsService extends Actor {

  def receive = {
    case GetRetweets =>
      sender ! "Retweets Test"
    case GetShow =>
      sender ! "Show Test"
    case GetOembed =>
      sender ! "Oembed Test"
    case PostRetweet =>
      sender ! "Posted Retweet Test"
    case PostUpdate(tweet, favorite) =>
      //println("Tweet Received : " + tweet)
      //sender ! "Update Test"
    case PostUpdateWithMedia =>
      sender ! "Update with media Test"
    case PostDestroy =>
      sender ! "Destroy Test"
    case _ => println("Unknown message received")
  }
}