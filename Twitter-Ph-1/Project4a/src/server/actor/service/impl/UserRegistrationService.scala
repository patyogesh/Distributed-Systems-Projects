package server.actor.service.impl

import akka.actor.Actor
import common.Tweet
import common.UserProfile
import akka.actor.ActorRef
import server.messages.Request
import common.ServiceRequest
import server.messages.RegisterUser
import common.UserProfile

class UserRegistrationService(userProfilesMap: scala.collection.mutable.Map[String, UserProfile], tweetsMap: scala.collection.mutable.Map[String, Tweet]) extends Actor {

  def receive = {
    case RegisterUser(userName: String) =>
      val userProfile: UserProfile = new UserProfile(userName, List[String](), List[String](), List[String]())
      userProfilesMap += userName -> userProfile
    case _ => println("Unknown message received")
  }
}