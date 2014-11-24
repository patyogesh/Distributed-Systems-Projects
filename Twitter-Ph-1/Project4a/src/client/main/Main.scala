package client.main

import akka.actor.ActorSystem
import akka.actor.Props
import client.actor.ClientActor
import akka.actor.ActorRef
import client.messages.TweetToServer
import test.Hello
import com.typesafe.config.ConfigFactory
import common.Constants
import server.messages.Request
import common.ServiceRequest
import server.messages.RegisterUser

object Main {

  def main(args: Array[String]) {

    val followers = Array(8, 7, 7, 5, 5, 3, 3, 1, 1, 1)
    val numberOfTweetsPerDay = Array(9, 4, 3, 2, 2, 1, 1, 1, 1, 1)
    val clients: Int = 284000000
    val sampleSize: Int = 10
    val localAddress: String = java.net.InetAddress.getLocalHost.getHostAddress()
    val hostAddress: String = args(0)
    val constants = new Constants()
    val serverAddress: String = "akka.tcp://Project4aServer@" + hostAddress + ":" + constants.SERVER_PORT  + "/user" //args(0)
    val offset = 24 * 3600 / clients

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

    for (i <- 0 to clients - 1) {
      var client = system.actorOf(Props(new ClientActor(serverAddress, followers((i % sampleSize)), numberOfTweetsPerDay((i % sampleSize)), i * offset)), "Client" + i)
      val servicePath = serverAddress + "/UserRegistrationRouter"
      val server = system.actorSelection(servicePath)
      server ! RegisterUser("Client"+i)
    }
    

  }
}