package client.main

import akka.actor.ActorSystem
import akka.actor.Props
import client.actor.ClientActor

object Main {

  def main(args: Array[String]) {
    
    val followers = Array(8, 7, 7, 5, 5, 3, 3, 1, 1, 1)
    val numberOfTweetsPerDay = Array(9, 4, 3, 2, 2, 1, 1, 1, 1, 1)
    val clients: Int = 4//284000000
    val sampleSize: Int = 10
    val hostAddress: String = args(0)
    val TwitterServerPort = 4030
    val serverAddress: String = "akka.tcp://Project4aServer@" + hostAddress + ":" + TwitterServerPort + "/user" //args(0)
    val offset = 24 * 3600 / clients

    val system = ActorSystem("Project4aClient")

    for (i <- 0 to clients-1) {
      var client = system.actorOf(Props(new ClientActor(serverAddress, followers((i % sampleSize)), numberOfTweetsPerDay((i % sampleSize)), i * offset)), "Client" + i)
    }
  }
}