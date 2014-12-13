package main.scala.akka.client.actor

import akka.actor.Actor
import akka.actor.Props
import main.scala.common.Start
import scala.collection.mutable.ListBuffer
import akka.actor.ActorRef

//#This class creates client actors to simulate users on client side.
class ClientActorFactory(clients: Int, serverAddress: String, followers: Array[Int], sampleSize: Int, numberOfTweetsPerDay: Array[Int], offset: Double, localAddress: String, timeMultiplier: Double, peakActor: ActorRef) extends Actor {

  val clientActors = ListBuffer[ActorRef]()
  for (i <- 0 to clients - 1) {
    clientActors += context.system.actorOf(Props(new ClientActor(serverAddress, followers((i % sampleSize)), numberOfTweetsPerDay((i % sampleSize)), i * offset, "Client" + i + "@" + localAddress, clients, timeMultiplier)), "Client" + i + "@" + localAddress)
  }
  def receive = {
    case Start =>
      println("Registration of clinets on server successful. Starting Load on server.")
      if (peakActor != null)
        peakActor ! Start
      clientActors.foreach(ref => ref ! Start)
    case _ =>
      println("Unknown message received at client actor factory")
  }
}