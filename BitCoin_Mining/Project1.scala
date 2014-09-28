package bitcoin

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Actor
import akka.actor.ActorRef
import akka.routing.RoundRobinRouter
import scala.collection.mutable.Stack
import scala.util.Random

//# Main
object Project1 {

  //#Either can be used as gator id for string generation
  private val GatorID = "bhavnesh.gugnani"
  //private val GatorID = "yogeshpatil20"

  //#Port of main server machine  
  private val BitCoinMiningPort = 8000
  //#Port of local nodes
  private val LocalMasterPort = 2346

  def main(args: Array[String]) {
    val mask: Int = args(0).toInt //#Number of 0 required at start of hash
    val hostAddress: String = java.net.InetAddress.getLocalHost.getHostAddress()

    if (args.length == 1) { //start bit coin distributed system

      val configString = """akka {
  actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }
  remote {
    enabled-transports = ["akka.remote.netty.tcp"]
    netty.tcp {
      hostname = """ + hostAddress + """
      port = """ + BitCoinMiningPort + """
    }
 }
}"""

      val configuration = ConfigFactory.parseString(configString)
      val system = ActorSystem("BitCoinMining", ConfigFactory.load(configuration))
      val superMasterReferencePath = "akka.tcp://BitCoinMining@" + hostAddress + ":" + BitCoinMiningPort + "/user/SuperMaster"
      val masterReferencePath = "akka.tcp://BitCoinMining@" + hostAddress + ":" + BitCoinMiningPort + "/user/Master"

      val superMasterRef = system.actorOf(Props[SuperMaster], "SuperMaster")
      superMasterRef ! StartMining
      //# Super master is on same machine , so super master reference is needed . 
      val masterRef = system.actorOf(Props(new Master(superMasterReferencePath, masterReferencePath, 1, GatorID, mask)), "Master")

    } else if (args.length == 2) {
      var superMasterIP = args(1)

      val configString = """akka {
  actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }
  remote {
    enabled-transports = ["akka.remote.netty.tcp"]
    netty.tcp {
      hostname = """ + hostAddress + """
      port = """ + LocalMasterPort + """
    }
 }
}"""

      val configuration = ConfigFactory.parseString(configString)
      val system = ActorSystem("Node", ConfigFactory.load(configuration))
      val superMasterReferencePath = "akka.tcp://BitCoinMining@" + superMasterIP + ":" + BitCoinMiningPort + "/user/SuperMaster"
      val masterReferencePath: String = "akka.tcp://Node@" + java.net.InetAddress.getLocalHost.getHostAddress + ":" + LocalMasterPort + "/user/Master"
      //# Since super master is on different machine , reference path is needed
      val masterRef = system.actorOf(Props(new Master(superMasterReferencePath, masterReferencePath, 0, GatorID, mask)), "Master")

    }
  }

}

//# SuperMaster
class SuperMaster extends Actor {

  var connectionReferences = IndexedSeq.empty[(String, Int, String, String)]
  var textLength: Int = 2

  //#Keeps track of work assigned to any master and retrive task in case any master fails 
  val assignedWork: Map[String, Int] = Map()
  val failedWork = new Stack[Int]

  def receive = {
    case StartMining =>
      println("Bit Coin Mining Started.")
    case MasterRegistration(masteruuid, isMasterLocal, superMasterReferencePath, masterReferencePath) =>
      println("New Master Discovered")
      sender() ! MasterRegistered
      var connectionRef = (masteruuid, isMasterLocal, superMasterReferencePath, masterReferencePath)
      connectionReferences = connectionReferences :+ connectionRef
      distributeWorkToMaster(connectionRef)
    case BitCoinFound(str: String, hash: String) =>
      //#Print found Bit Coin
      println(str + "\t" + hash)
    case Result(connectionRef: (String, Int, String, String)) =>
      //#Previous work complete . Assign new work
      assignedWork - (connectionRef._1)
      distributeWorkToMaster(connectionRef)
    case WorkFailed(masteruuid: String) =>
      failedWork push (assignedWork(masteruuid))
      assignedWork - (masteruuid)
    case _ => println("Unknown message received")
  }

  private def distributeWorkToMaster(connectionRef: (String, Int, String, String)): Unit = {
    //#Choose between new task that can be assigned if any older work has not failed
    var currTask = textLength
    if (!failedWork.isEmpty) {
      currTask = failedWork pop
    } else {
      textLength += 1
    }
    assignedWork + (connectionRef._1 -> currTask)
    if (connectionRef._2 == 0) { //#Remote master
      val masterSelection = context.actorSelection(connectionRef._4)
      masterSelection ! StartWork(connectionRef, currTask)
    } else { //#Local master
      context.actorFor(connectionRef._4) ! StartWork(connectionRef, currTask)
    }
  }
}

case class StartMining()
case class MasterRegistration(val masteruuid: String, val isMasterLocal: Int, val superMasterReferencePath: String, val masterReferencePath: String)
case class WorkFailed(val masteruuid: String)

//#Master
class Master(val superMasterReferencePath: String, val masterReferencePath: String, val isMasterLocal: Int, val gatorID: String, val mask: Int) extends Actor {

  var connectionRef: (String, Int, String, String) = null

  val cores = Runtime.getRuntime().availableProcessors();
  val numberOfWorkers = cores * 2
  var workComplete = false

  def uuid = java.util.UUID.randomUUID.toString

  val workerRouter = context.actorOf(
    Props(new Worker(mask, gatorID)).withRouter(RoundRobinRouter(numberOfWorkers)), name = "WorkerRouter")

  override def preStart = {
    //#Register to Super master.
    if (isMasterLocal == 0) { //#Connect to Remote Server 
      println("trying to remote register")
      val selection = context.actorSelection(superMasterReferencePath)
      selection ! MasterRegistration(uuid, isMasterLocal, superMasterReferencePath, masterReferencePath)
    } else { //#Connect to super master on same machine before asking work
      context.actorFor(superMasterReferencePath) ! MasterRegistration(uuid, isMasterLocal, superMasterReferencePath, masterReferencePath)
    }
  }

  override def postStop = {
    if (!workComplete) {
      val selection = context.actorSelection(superMasterReferencePath)
      selection ! WorkFailed(uuid)
    }
  }

  def receive = {
    case MasterRegistered =>
    //println("Master registered to supermaster.")
    case StartWork(connectionRef, textLength) =>
      //#Start task assigned from main server
      this connectionRef = connectionRef
      workComplete = false //#New work assigned 
      distributeToWorkers(textLength)
    case BitCoinFound(str: String, hash: String) =>
      //#send bit coin found to main server while master continues to manage existing workers
      if (connectionRef._2 == 0) { //#Remote Super master
        val superMasterSelection = context.actorSelection(superMasterReferencePath)
        superMasterSelection ! BitCoinFound(str, hash)
      } else { //#Local master
        context.actorFor(superMasterReferencePath) ! BitCoinFound(str, hash)
      }
    case _ => println("Unknown message received")
  }

  def distributeToWorkers(textLength: Int) = {
    //#Possible printable characters in string have ASCII code between 33 and 126
    for (i <- 33 to 126)
      workerRouter ! WorkerWork(textLength - 1, i.toChar)

    //#Update central server that work is complete . Assign new work .  
    if (connectionRef._2 == 0) { //#Remote Super master
      val superMasterSelection = context.actorSelection(superMasterReferencePath)
      superMasterSelection ! Result(connectionRef)
    } else {
      context.actorFor(superMasterReferencePath) ! Result(connectionRef)
    }
    workComplete = true
  }
}

case class RegisterToSuperMaster(actorRef: ActorRef)
case class StartWork(masterRef: (String, Int, String, String), textLength: Int)
case class Result(masterRef: (String, Int, String, String))
case class BitCoinFound(str: String, hash: String)
case class TaskCompleted()
case class MasterRegistered()

//#Worker
class Worker(mask: Int, gatorId: String) extends Actor {

  private val sha = java.security.MessageDigest.getInstance("SHA-256")
  private val pattern = "(^0{" + mask + "}.*)"

  def receive = {
    case WorkerWork(textLength, startChar) =>
      generatePossibleStrings(gatorId + startChar, textLength, 0)
    case _ => println("Unknown message received")
  }

  private def hexDigest(s: String): String = { //#Returns SHA-256 hash in hex
    sha.digest(s.getBytes("UTF-8"))
      .foldLeft("")((s: String, b: Byte) => s +
        Character.forDigit((b & 0xf0) >> 4, 16) +
        Character.forDigit(b & 0x0f, 16))
  }

  private def generatePossibleStrings(str: String, textLength: Int, currIndex: Int): Unit = {
    //#Iterative backtracking algorithm used to check all possibilities of input for checking if a possible bitcoin
    val possibleStrings = Array.fill[Int](textLength)(33)
    do {
      try {
        var temp = str
        possibleStrings.foreach { temp += _.toChar }

        val hashValue = hexDigest(temp)
        val regex = pattern.r
        val regex(value) = hashValue
        if (value != "") {
          //#send bit coin found to master while worker still mines more bit coins
          sender() ! BitCoinFound(str, hashValue)
        }
      } catch {
        case io: scala.MatchError => //No match found for mask bits in hash value 
      }
      var incrementIndex = possibleStrings.length - 1
      while (incrementIndex >= 0) {
        possibleStrings(incrementIndex) += 1
        if (possibleStrings(incrementIndex) > 126) {
          if (incrementIndex > 0) {
            possibleStrings(incrementIndex) = 33
          }
          incrementIndex -= 1
        } else {
          incrementIndex = -1
        }
      }

    } while (possibleStrings(0) <= 126)
  }

}

case class WorkerWork(textLength: Int, startChar: Char)
