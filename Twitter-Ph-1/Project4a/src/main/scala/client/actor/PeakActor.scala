package main.scala.client.actor

import akka.actor.Actor
import main.scala.common.TweetToServer
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import main.scala.common.AkkaRequest
import main.scala.common.Start

//#This actor simulates a sudden peak load on server by having a large number of followers and tweeting.
class PeakActor(startTime: Int, interval: Int, serverAddress: String, selfPath: String, name: String) extends Actor {

  import context.dispatcher

  def receive = {
    case Start =>
      val spike = context.system.scheduler.schedule((startTime * 1000) milliseconds, (interval * 1000) milliseconds, self, TweetToServer)
    case TweetToServer =>
      val servicePath = serverAddress + "/TweetsServiceRouter"
      val server = context.actorSelection(servicePath)
      println("creating spike load.")
      val uuid = java.util.UUID.randomUUID().toString()
      server ! new AkkaRequest(uuid, selfPath + name, "PostUpdate", name, "", "blah!")
    case _ =>
      println("Unknown message received in Peak actor")
  }
}