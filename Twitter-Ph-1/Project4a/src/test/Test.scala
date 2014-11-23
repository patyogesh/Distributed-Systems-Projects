package test

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import com.typesafe.config.ConfigFactory

object Test {

  def main(args: Array[String]) {

    val hostAddress: String = java.net.InetAddress.getLocalHost.getHostAddress()
    val TwitterServerPort = 4030
    val configString = """akka {
  actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }
  remote {
    enabled-transports = ["akka.remote.netty.tcp"]
    netty.tcp {
      hostname = """ + hostAddress + """
      port = """ + TwitterServerPort + """
    }
 }
}"""

    val configuration = ConfigFactory.parseString(configString)
    val system = ActorSystem("Test", ConfigFactory.load(configuration))
    val actor = system.actorOf(Props[Master], "master");
    actor ! Send(args(0))
    //println(actor.path)
  }
}

class Master extends Actor {

  def receive = {
    case Send(hostAddress) =>
      val serverAddress: String = "akka.tcp://Test@" + hostAddress + ":" + 4030 + "/user/master"
      val selection = context.actorSelection(serverAddress)
      selection ! Hello
    case _ =>
      println("Message received")
  }
}

sealed trait Messages
case class Send(addres: String) extends Messages
case class Hello() extends Messages