package client.actor

import akka.actor.Actor
import akka.actor.Props
import common.Start
import scala.collection.mutable.ListBuffer
import akka.actor.ActorRef

class ClientActorFactory(clients: Int, serverAddress: String, followers: Array[Int], sampleSize: Int, numberOfTweetsPerDay: Array[Int], offset: Double, localAddress: String, timeMultiplier: Double) extends Actor {

  val clientActors = ListBuffer[ActorRef]()
  for (i <- 0 to clients - 1) {
      clientActors += context.system.actorOf(Props(new ClientActor(serverAddress, followers((i % sampleSize)), numberOfTweetsPerDay((i % sampleSize)), i * offset, "Client" + i + "@" + localAddress, clients, timeMultiplier)), "Client" + i + "@" + localAddress)
    }
	def receive = {
	  case Start =>
	    clientActors.foreach(ref => ref ! Start)
	  case _ =>
	    println("Unknown message received at client actor factory")
	}
}