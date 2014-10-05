package actors

import akka.actor.Actor
import akka.actor.ActorRef
import messages.UpdateFullNetworkActorNeighbours
import messages.Gossip
import messages.PushSum
import constants.Constants
import messages.ShutDown
import akka.actor.ActorSystem
import vo.ConvergenceCounter
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import messages.GossipIn
import messages.GossipOut
import messages.PushSumIn
import messages.PushSumOut

class FullNetworkActor(system: ActorSystem, convergenceCounter: ConvergenceCounter, s: BigDecimal, w: BigDecimal) extends Actor with Constants {

  //#Neighbours
  var allNetworkActor: Array[ActorRef] = null
  var currIndex: Int = -1
  //#From last round
  var oldS: BigDecimal = s
  var oldW: BigDecimal = w
  //#For current round 
  var newS: BigDecimal = s
  var newW: BigDecimal = w
  //#Last 3 differences between s/w
  var last3: Array[BigDecimal] = Array(-1, -1, -1)
  private var count: Int = 0
  var rand = new scala.util.Random
  var isGossiping = false
  var isPushingSum = false
  var hasConverged = false
  var startTime: Long = -1

  def receive() = {
    case UpdateFullNetworkActorNeighbours(allNetworkActor, currIndex) =>
      this.allNetworkActor = allNetworkActor
      this.currIndex = currIndex
    case GossipIn(startTime) =>
      if (convergenceCounter.actorsNotConvergedCounter == 1) {
        printf("System Converged in time : " + (System.currentTimeMillis() - startTime) + " milli seconds")
        context.system.shutdown
        //startShutDownWave()
      } else if (!isGossiping) {
        hasConverged = true
        convergenceCounter.actorsNotConvergedCounter -= 1
        isGossiping = true
        this.startTime = startTime
        import context.dispatcher
        val wakeUp = context.system.scheduler.schedule(0 milliseconds, 100 milliseconds, self, GossipOut)
      }
    case GossipOut =>
      gossipOut()
    case PushSumIn(s, w, startTime) =>
      newS += s
      newW += w
      if (!isPushingSum) {
        isPushingSum = true
        this.startTime = startTime
        import context.dispatcher
        val wakeUp = context.system.scheduler.schedule(0 milliseconds, 100 milliseconds, self, PushSumOut)
      }
    case PushSumOut =>
      pushSumOut()
    case ShutDown =>
      context.stop(self)
    case _ => println("Invalid message")
  }

  def gossipOut() = {
    var sent = false
    while (!sent) {
      val randValue = rand.nextInt(allNetworkActor.length)
      if (randValue != currIndex && allNetworkActor(randValue) != null) {
        allNetworkActor(randValue) ! GossipIn(startTime)
        sent = true
      }
    }
  }

  def pushSumOut() = {
    if (!hasConverged) {
      val outS = newS / 2
      val outW = newW / 2
      newS -= outS
      newW -= outW

      if (last3(0) >= 0.0000000001 || last3(1) >= 0.0000000001 || last3(2) >= 0.0000000001 ||
        last3(0) == -1) {
        println("Pushing sum : " + outS + "/" + outW)
        var sent = false
        while (!sent) {
          val randValue = rand.nextInt(allNetworkActor.length)
          if (randValue != currIndex && allNetworkActor(randValue) != null) {
            allNetworkActor(randValue) ! PushSumIn(outS, outW, startTime)
            sent = true
          }
        }
        last3(0) = last3(1)
        last3(1) = last3(2)
        last3(2) = (oldS / oldW) - (newS / newW)
        if (last3(0) < 0.0000000001 && last3(1) < 0.0000000001 && last3(2) < 0.0000000001) {
          hasConverged = true
          convergenceCounter.actorsNotConvergedCounter -= 1
        }
        oldS = newS
        oldW = newW
        if (convergenceCounter.actorsNotConvergedCounter == 0) {
          val endTime: Long = System.currentTimeMillis()
          println("Convergence Time : " + (endTime - startTime))
          context.system.shutdown
          //startShutDownWave
        }
      }
    }
  }

  def startShutDownWave() = {
    for (ref <- allNetworkActor)
      ref ! ShutDown
  }

  /*def receive() = {
    case UpdateFullNetworkActorNeighbours(allNetworkActor, currIndex) =>
      this.allNetworkActor = allNetworkActor
      this.currIndex = currIndex
    case Gossip =>
      if (count < NumberOfGossips) {
        count = count + 1
        println("Gossiping " + count)
        Thread sleep SleepTime

        var sent = false
        while (!sent) {
          val randValue = rand.nextInt(allNetworkActor.length)
          if (randValue != currIndex && allNetworkActor(randValue) != null) {
            allNetworkActor(randValue) ! Gossip
            sent = true
          }
        }
      }
    case PushSum(s, w, startTime) =>
      if(this.startTime == -1)
        this.startTime  = startTime
      if (last3(0) >= 0.0000000001 || last3(1) >= 0.0000000001 || last3(2) >= 0.0000000001 ||
        last3(0) == -1) {
        println("Pushing")
        this.localS += s
        this.localW += w
        var sent = false
        var oldSByW = localS / localW
        val pushedS = localS / 2
        localS -= localS / 2
        val pushedW = localW / 2
        localW -= localW / 2
        while (!sent) {
          val randValue = rand.nextInt(allNetworkActor.length)
          if (randValue != currIndex && allNetworkActor(randValue) != null) {
            allNetworkActor(randValue) ! PushSum(pushedS, pushedW, startTime)
            sent = true
          }
        }
        var newSByW = localS / localW
        last3(0) = last3(1)
        last3(1) = last3(2)
        last3(2) = newSByW - oldSByW
        println("Pushing sum : " + pushedS + "/" + pushedW)
      }
    case _ => println("Invalid message")
    case ShutDown =>
      context.stop(self)
  }*/
}