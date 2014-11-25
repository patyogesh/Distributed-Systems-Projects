package client.main

import akka.actor.ActorSystem
import akka.actor.Props
import client.actor.ClientActor
import akka.actor.ActorRef
import com.typesafe.config.ConfigFactory
import common.Constants
import common.ServiceRequest
import common.RegisterUsers
import client.actor.ClientActorFactory
import client.actor.PeakActor

object Main {

  def main(args: Array[String]) {
    val hostAddress: String = args(0)

    //Cranking factors from input
    val timeMultiplier: Double = args(1).toDouble
    val userCountMultiplier: Double = args(2).toDouble
    val tweetsCountMultiplier: Double = args(3).toDouble

    //default values
    val followers = Array(8, 7, 7, 5, 5, 3, 3, 1, 1, 1)
    val numberOfTweetsPerDay = Array(9000, 4000, 3000, 2000, 2000, 1000, 1000, 1000, 1000, 1000)
    var clients: Int = 250000 //2840000 //00000
    val sampleSize: Int = 10

    //Scale tweets
    for (i <- 0 to numberOfTweetsPerDay.length - 1)
      numberOfTweetsPerDay(i) = (numberOfTweetsPerDay(i) * tweetsCountMultiplier).toInt

    //Scale User count
    clients = (clients * userCountMultiplier).toInt

    val localAddress: String = java.net.InetAddress.getLocalHost.getHostAddress()
    val constants = new Constants()
    val serverAddress: String = "akka.tcp://Project4aServer@" + hostAddress + ":" + constants.SERVER_PORT + "/user"

    //Scale time
    val offset = (24 * 3600) / (clients * timeMultiplier)

    val configString = """akka {
  actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }
  remote {
    enabled-transports = ["akka.remote.netty.tcp"]
    netty.tcp {
      hostname = """ + localAddress + """
      port = """ + constants.SERVER_PORT + """
    }
 }
}"""
    val configuration = ConfigFactory.parseString(configString)
    val system = ActorSystem("Project4aClient", ConfigFactory.load(configuration))

    //Peak Load arguments. 
    var peakActor: ActorRef = null
    var peakActorName: String = ""
    var peakActorFollowersCount: Int = 0
    try {
      val startTime: Int = args(4).toInt
      val interval: Int = args(5).toInt
      peakActorFollowersCount = args(6).toInt
      val selfPath = "akka.tcp://Project4aClient@" + localAddress + ":" + constants.SERVER_PORT + "/user/PeakActor"
      peakActor = system.actorOf(Props(new PeakActor(startTime, interval, serverAddress, selfPath, "PeakActor@" + localAddress)), "PeakActor")
      peakActorName = "PeakActor"
    } catch {
      case ex: java.lang.ArrayIndexOutOfBoundsException => //
    }

    val clientActorFactory = system.actorOf(Props(new ClientActorFactory(clients, serverAddress, followers, sampleSize, numberOfTweetsPerDay, offset, localAddress, timeMultiplier, peakActor)), "ClientActorFactory")
    val clientFactoryPath: String = "akka.tcp://Project4aClient@" + localAddress + ":" + constants.SERVER_PORT + "/user/ClientActorFactory"

    val server = system.actorSelection(serverAddress + "/UserRegistrationRouter")
    server ! RegisterUsers(localAddress, clients, clientFactoryPath, followers, sampleSize, peakActorName, peakActorFollowersCount)

  }
}