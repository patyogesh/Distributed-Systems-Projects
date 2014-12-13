package main.scala.spray.client.main

import akka.actor.ActorSystem
import akka.actor.Props

object Main {

  def main(args: Array[String]) {
    val system = ActorSystem()
    val actor = system.actorOf(Props[SprayActor], "client")
  }
}