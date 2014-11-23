package client.actor

import akka.actor.Actor
import akka.actor.ActorSelection
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import client.messages.TweetToServer
import server.messages.PostUpdate

class ClientActor(serverAddress: String, followers: Int, tweetsPerDay: Int, offset: Int) extends Actor {

  import context.dispatcher
  val server: ActorSelection = context.actorSelection(serverAddress)
  val tweetTimeout = (24*3600/tweetsPerDay)+offset
  val tweet = context.system.scheduler.schedule(0  milliseconds, tweetTimeout*1000  milliseconds, self, TweetToServer)
  
  def receive = {
    case TweetToServer =>
      server ! PostUpdate("chumma chumma dede!")
    case _ =>
      println("Unknown Message")
  }
}