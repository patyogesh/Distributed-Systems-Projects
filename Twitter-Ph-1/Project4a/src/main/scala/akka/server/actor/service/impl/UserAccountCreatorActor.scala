package main.scala.akka.server.actor.service.impl

import akka.actor.Actor
import scala.collection.mutable.ListBuffer
import main.scala.common.UserProfile
import main.scala.common.CreateUserProfiles
import main.scala.common.TaskComplete

//#Helps registering users on server faster by dividing task among actors
class UserAccountCreatorActor extends Actor {

  def receive = {
    case CreateUserProfiles(jobId, start, end, ip, userProfilesMap, followers, sampleSize, senderPath) =>
      for (i <- start to end) {
        val userProfile: UserProfile = new UserProfile("Client" + i + "@" + ip, new ListBuffer[String], new ListBuffer[String], new ListBuffer[String])
        userProfilesMap += "Client" + i + "@" + ip -> userProfile
        val followerCount: Int = followers(i % sampleSize)
        val followerList: ListBuffer[String] = userProfile.followers
        for (k <- Math.max(0, i - followerCount) to i - 1) {
          followerList += "Client" + k + "@" + ip
        }
      }
      
      val sender = context.actorSelection(senderPath)
      sender ! TaskComplete(jobId)
    case _ =>
      println("Unknown message received at User Account creater actor.")
  }
}