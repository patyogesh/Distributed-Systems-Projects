package client.main

import akka.actor.ActorSystem
import akka.actor.Props
import client.actor.ClientActor
import akka.actor.ActorRef
import test.Hello
import com.typesafe.config.ConfigFactory
import common.Constants
import common.ServiceRequest
import common.RegisterUsers

object Main {

  def main(args: Array[String]) {

    val followers = Array(8, 7, 7, 5, 5, 3, 3, 1, 1, 1)
    val numberOfTweetsPerDay = Array(9000, 4000, 3000, 2000, 2000, 1000, 1000, 1000, 1000, 1000)
    val clients: Int = 2840000 //00
    val sampleSize: Int = 10
    val localAddress: String = java.net.InetAddress.getLocalHost.getHostAddress()
    val hostAddress: String = args(0)
    val constants = new Constants()
    val serverAddress: String = "akka.tcp://Project4aServer@" + hostAddress + ":" + constants.SERVER_PORT + "/user" //args(0)
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

    val server = system.actorSelection(serverAddress + "/UserRegistrationService")
    server ! RegisterUsers(localAddress, clients)

    for (i <- 0 to clients - 1) {
      var client = system.actorOf(Props(new ClientActor(serverAddress, followers((i % sampleSize)), numberOfTweetsPerDay((i % sampleSize)), i * offset, "Client" + i + "@" + localAddress, clients)), "Client" + i + "@" + localAddress)
    }

  }
}