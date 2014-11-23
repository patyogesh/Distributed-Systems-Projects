package server.actor.service.impl

import akka.actor.Actor
import server.messages._
import akka.actor.actorRef2Scala

class TimelineService extends Actor {

  def receive = {
    case GetMentionsTimeline =>
      sender ! "Mentions Timeline Test"
    case GetHomeTimeline =>
      sender ! "Home Timeline Test"
    case GetUserTimeline =>
      sender ! "User Timeline Test"
    case _ => println("Unknowk message received");
  }
}