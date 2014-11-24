package server.actor.service.impl

import akka.actor.Actor
import common.Tweet
import common.UserProfile
import akka.actor.ActorRef
import common.ServiceRequest
import common.UserProfile
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import common.UpdateRegisteredUserCount
import common.RegisterUser
import common.UserCount
import common.RegisterUsers

class UserRegistrationService(loadMonitor: ActorRef, userProfilesMap: scala.collection.mutable.Map[String, UserProfile], tweetsMap: scala.collection.mutable.Map[String, Tweet]) extends Actor {
  import context.dispatcher

  var usersRegistered: Int = 0
  val userRegistered = context.system.scheduler.schedule(0 milliseconds, 2000 milliseconds, self, UpdateRegisteredUserCount)

  def receive = {
    case RegisterUser(userName: String) =>
      val userProfile: UserProfile = new UserProfile(userName, List[String](), List[String](), List[String]())
      userProfilesMap += userName -> userProfile
      usersRegistered += 1
    case RegisterUsers(ip: String, clients: Int) =>
      for (i <- 0 to clients - 1) {
        val userProfile: UserProfile = new UserProfile("Client" + i + "@" + ip, List[String](), List[String](), List[String]())
        userProfilesMap += "Client" + i + "@" + ip -> userProfile
      }
      usersRegistered += clients
    case UpdateRegisteredUserCount =>
      loadMonitor ! UserCount(usersRegistered)
      usersRegistered = 0
    case _ => println("Unknown message received in User Registration service.")
  }
}