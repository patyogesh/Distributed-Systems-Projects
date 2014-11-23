package client.actor

import akka.actor.Actor
import akka.actor.ActorSelection
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import client.messages.TweetToServer
import server.messages.PostUpdate
import client.messages.TweetToServer
import server.messages.Request
import test.Hello

class ClientActor(serverAddress: String, followers: Int, tweetsPerDay: Int, offset: Int) extends Actor {

  import context.dispatcher
  
  val tweetTimeout = (24 * 3600 / tweetsPerDay) + offset
  val tweet = context.system.scheduler.schedule(0 milliseconds, tweetTimeout * 1000 milliseconds, self, TweetToServer)

  def receive = {
    case TweetToServer =>
      val servicePath = serverAddress + "/TweetsServiceRouter"
      val server = context.actorSelection(servicePath)
      server ! PostUpdate("Chumma Chumma dede!", followers)
    case _ =>
      println("Unknown Message")
  }
}