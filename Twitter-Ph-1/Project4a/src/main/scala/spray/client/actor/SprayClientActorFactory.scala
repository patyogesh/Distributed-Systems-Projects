package main.scala.spray.client.actor

import scala.collection.mutable.ListBuffer
import akka.actor.ActorRef
import main.scala.common.Start
import akka.actor.Actor
import akka.actor.actorRef2Scala
import akka.actor.Props

class SprayClientActorFactory(clients: Int, serverAddress: String, followers: Array[Int], sampleSize: Int, numberOfTweetsPerDay: Array[Int], offset: Double, localAddress: String, timeMultiplier: Double, peakActor: ActorRef) extends Actor {

  val clientActors = ListBuffer[ActorRef]()
  for (i <- 0 to clients - 1) {
    clientActors += context.system.actorOf(Props(new SprayClientActor(serverAddress, followers((i % sampleSize)), numberOfTweetsPerDay((i % sampleSize)), i * offset, "SprayClient" + i + "@" + localAddress, clients, timeMultiplier)), "SprayClient" + i + "@" + localAddress)
  }
  def receive = {
    case Start =>
      println("Registration of clients on server successful. Starting Load on server.")
      if (peakActor != null)
        peakActor ! Start
      clientActors.foreach(ref => ref ! Start)
    case _ =>
      println("Unknown message received at client actor factory")
  }
}