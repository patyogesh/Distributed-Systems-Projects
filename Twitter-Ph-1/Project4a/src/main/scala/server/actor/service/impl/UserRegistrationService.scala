package main.scala.server.actor.service.impl

import akka.actor.Actor
import main.scala.common.Tweet
import main.scala.common.UserProfile
import akka.actor.ActorRef
import main.scala.common.ServiceRequest
import main.scala.common.UserProfile
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import main.scala.common.UpdateRegisteredUserCount
import main.scala.common.RegisterUser
import main.scala.common.UserCount
import main.scala.common.RegisterUsers
import scala.collection.mutable.ListBuffer
import main.scala.common.Start
import scala.collection.mutable.Map
import akka.actor.Props
import akka.routing.ActorRefRoutee
import akka.routing.RoundRobinRoutingLogic
import akka.routing.Router
import main.scala.common.CreateUserProfiles
import main.scala.common.TaskComplete

class UserRegistrationService(count: Int, loadMonitor: ActorRef, userProfilesMap: Map[String, UserProfile], tweetsMap: Map[String, Tweet]) extends Actor {
  import context.dispatcher

  var usersRegistered: Int = 0
  var jobID: Int = 0
  var jobMap = Map[Int, Job]()
  val useRegistered = context.system.scheduler.schedule(0 milliseconds, 2000 milliseconds, self, UpdateRegisteredUserCount)
  val userProfileCreatorRouter = {
    val routees = Vector.fill(count) {
      val r = context.actorOf(Props(new UserAccountCreatorActor()))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case RegisterUser(userName: String) =>
      val userProfile: UserProfile = new UserProfile(userName, new ListBuffer[String], new ListBuffer[String], new ListBuffer[String])
      userProfilesMap += userName -> userProfile
      usersRegistered += 1
    case RegisterUsers(ip: String, clients: Int, clientFactoryPath: String, followers: Array[Int], sampleSize: Int, peakActorName: String, peakActorFollowersCount: Int) =>
      /*for (i <- 0 to clients - 1) {
        val userProfile: UserProfile = new UserProfile("Client" + i + "@" + ip, new ListBuffer[String], new ListBuffer[String], new ListBuffer[String])
        userProfilesMap += "Client" + i + "@" + ip -> userProfile
        val followerCount: Int = followers(i % sampleSize)
        val followerList: ListBuffer[String] = userProfile.followers
        for (k <- Math.max(0, i - followerCount) to i - 1) {
          followerList += "Client" + k + "@" + ip
        }
      }
      usersRegistered += clients
      //Register Peak user profile for spike
      if (peakActorName != "") {
        val userProfile: UserProfile = new UserProfile(peakActorName + "@" + ip, new ListBuffer[String], new ListBuffer[String], new ListBuffer[String])
        userProfilesMap += peakActorName + "@" + ip -> userProfile
        val followerList: ListBuffer[String] = userProfile.followers
        for (i <- 0 to Math.min(clients - 1, peakActorFollowersCount - 1))
          followerList += "Client" + i + "@" + ip
        usersRegistered += 1
      }
      val factory = context.actorSelection(clientFactoryPath)
      factory ! Start*/

      
      val taskCount = userProfileCreatorRouter.routees.length
      val taskSize: Int = Math.ceil(clients / taskCount).toInt
      jobMap += jobID -> new Job(jobID, taskCount, taskSize, clientFactoryPath)
      for (i <- 0 to taskCount - 1) {
        userProfileCreatorRouter.route(CreateUserProfiles(jobID, i * taskSize, Math.min(((i + 1) * taskSize) - 1, clients - 1), ip, userProfilesMap, followers, sampleSize, self.path.toString()), sender)
      }
      jobID += 1

      //Register Peak user profile for spike
      if (peakActorName != "") {
        val userProfile: UserProfile = new UserProfile(peakActorName + "@" + ip, new ListBuffer[String], new ListBuffer[String], new ListBuffer[String])
        userProfilesMap += peakActorName + "@" + ip -> userProfile
        val followerList: ListBuffer[String] = userProfile.followers
        for (i <- 0 to Math.min(clients - 1, peakActorFollowersCount - 1))
          followerList += "Client" + i + "@" + ip
        usersRegistered += 1
      }
    case TaskComplete(jobID) =>
      val job: Job = jobMap.get(jobID).get
      job.remainingJobs -= 1
      usersRegistered += job.jobSize
      if (job.remainingJobs == 0) {
        val factory = context.actorSelection(job.clientFactoryPath)
        factory ! Start
      }
    case UpdateRegisteredUserCount =>
      loadMonitor ! UserCount(usersRegistered)
      usersRegistered = 0
    case _ => println("Unknown message received in User Registration service.")
  }
}

class Job(jobID: Int, numberOfJobs: Int, jobsize: Int, clientfactorypath: String) {
  var remainingJobs: Int = numberOfJobs
  val clientFactoryPath = clientfactorypath
  val jobSize = jobsize
}