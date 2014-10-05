package actors

import akka.actor.Actor
import akka.actor.ActorRef
import messages.UpdateLineActorNeighbours
import constants.Constants
import akka.actor.ActorSystem
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import messages.GossipIn
import messages.GossipOut
import vo.ConvergenceCounter
import messages.ShutDown
import messages.PushSumIn
import messages.PushSumOut

class LineActor(system: ActorSystem, convergenceCounter: ConvergenceCounter, s: BigDecimal, w: BigDecimal) extends Actor with Constants {

  //#Neighbours
  var prev: ActorRef = null
  var next: ActorRef = null
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
    case UpdateLineActorNeighbours(prev, next) =>
      this.prev = prev
      this.next = next
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

  private def gossipOut() = {
    var sent = false
    while (!sent) {
      if (rand.nextInt(2) == 0) {
        if (prev != null) {
          prev ! GossipIn(startTime)
          sent = true
        } else {
          next ! GossipIn(startTime)
          sent = true
        }
      } else {
        if (next != null) {
          next ! GossipIn(startTime)
          sent = true
        } else {
          prev ! GossipIn(startTime)
          sent = true
        }
      }
    }
  }

  private def pushSumOut() = {
    if (!hasConverged) {
      val outS = newS / 2
      val outW = newW / 2
      newS -= outS
      newW -= outW

      if (last3(0) >= 0.0000000001 || last3(1) >= 0.0000000001 || last3(2) >= 0.0000000001 ||
        last3(0) == -1) {
        //println("Pushing sum : " + outS + "/" + outW)
        var sent = false
        while (!sent) {
          if (rand.nextInt % 2 == 0) {
            if (prev != null) {
              prev ! PushSumIn(outS, outW, startTime)
              sent = true
            } else {
              next ! PushSumIn(outS, outW, startTime)
              sent = true
            }
          } else {
            if (next != null) {
              next ! PushSumIn(outS, outW, startTime)
              sent = true
            } else {
              prev ! PushSumIn(outS, outW, startTime)
              sent = true
            }
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
          println("Convergence Time for Push-Sum is : " + (endTime - startTime) + "milli seconds")
          context.system.shutdown
          //startShutDownWave
        }
      }
    }
  }

  def startShutDownWave() = {
    prev ! ShutDown
    next ! ShutDown
    self ! ShutDown
  }
}