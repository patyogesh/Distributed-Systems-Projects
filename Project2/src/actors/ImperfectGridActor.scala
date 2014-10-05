package actors

import akka.actor.Actor
import constants.Constants
import messages.PushSum
import akka.actor.ActorRef
import messages.UpdateImperfectGridActorNeighbours
import messages.Gossip
import messages.ShutDown
import akka.actor.ActorSystem
import vo.ConvergenceCounter
import messages.UpdateLineActorNeighbours
import messages.GossipIn
import messages.GossipOut
import messages.PushSumIn
import messages.PushSumOut
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit

class ImperfectGridActor(system: ActorSystem, convergenceCounter: ConvergenceCounter, s: BigDecimal, w: BigDecimal) extends Actor with Constants {

  //#Neighbours
  var top: ActorRef = null
  var left: ActorRef = null
  var right: ActorRef = null
  var bottom: ActorRef = null
  var random: ActorRef = null
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
    case UpdateImperfectGridActorNeighbours(top, left, right, bottom, random) =>
      this.top = top
      this.left = left
      this.right = right
      this.bottom = bottom
      this.random = random
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
      val randValue = rand.nextInt(5)
      if (randValue == 0) {
        if (top != null) {
          top ! GossipIn(startTime)
          sent = true
        }
      } else if (randValue == 1) {
        if (left != null) {
          left ! GossipIn(startTime)
          sent = true
        }
      } else if (randValue == 2) {
        if (right != null) {
          right ! GossipIn(startTime)
          sent = true
        }
      } else if (randValue == 3) {
        if (bottom != null) {
          bottom ! GossipIn(startTime)
          sent = true
        }
      } else if (randValue == 4) {
        if (random != null) {
          random ! GossipIn(startTime)
          sent = true
        }
      }
    }
  }

  def pushSumOut() = {
    if (!hasConverged) {
      val outS = newS / 2
      val outW = newW / 2
      oldS = newS - outS
      oldW = newW - outW
      
      if (last3(0) >= 0.0000000001 || last3(1) >= 0.0000000001 || last3(2) >= 0.0000000001 ||
        last3(0) == -1) {
        println("Pushing sum : " + outS + "/" + outW)
        var sent = false
        while (!sent) {
          val randValue = rand.nextInt(4)
          if (randValue == 0) {
            if (top != null) {
              top ! PushSumIn(outS, outW, startTime)
              sent = true
            }
          } else if (randValue == 1) {
            if (left != null) {
              left ! PushSumIn(outS, outW, startTime)
              sent = true
            }
          } else if (randValue == 2) {
            if (right != null) {
              right ! PushSumIn(outS, outW, startTime)
              sent = true
            }
          } else if (randValue == 3) {
            if (bottom != null) {
              bottom ! PushSumIn(outS, outW, startTime)
              sent = true
            }
          } else if (randValue == 4) {
            if (random != null) {
              random ! PushSumIn(outS, outW, startTime)
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
          println("Convergence Time : " + (endTime - startTime))
          context.system.shutdown
          //startShutDownWave
        }
      }
    }
  }

  def startShutDownWave() = {
    top ! ShutDown
    left ! ShutDown
    right ! ShutDown
    bottom ! ShutDown
    random ! ShutDown
    self ! ShutDown
  }

  /*def receive() = {
    case UpdateImperfectGridActorNeighbours(top, left, right, bottom, random) =>
      this.top = top
      this.left = left
      this.right = right
      this.bottom = bottom
      this.random = random
    case Gossip =>
      if (count < NumberOfGossips) {
        count = count + 1
        println("Gossiping " + count)
        Thread sleep SleepTime

        var sent = false
        while (!sent) {
          val randValue = rand.nextInt(5)
          if (randValue == 0) {
            if (top != null) {
              top ! Gossip
              sent = true
            }
          } else if (randValue == 1) {
            if (left != null) {
              left ! Gossip
              sent = true
            }
          } else if (randValue == 2) {
            if (right != null) {
              right ! Gossip
              sent = true
            }
          } else if (randValue == 3) {
            if (bottom != null) {
              bottom ! Gossip
              sent = true
            }
          } else if (randValue == 4) {
            if (random != null) {
              random ! Gossip
              sent = true
            }
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
          val randValue = rand.nextInt(4)
          if (randValue == 0) {
            if (top != null) {
              top ! PushSum(pushedS, pushedW, startTime)
              sent = true
            }
          } else if (randValue == 1) {
            if (left != null) {
              left ! PushSum(pushedS, pushedW, startTime)
              sent = true
            }
          } else if (randValue == 2) {
            if (right != null) {
              right ! PushSum(pushedS, pushedW, startTime)
              sent = true
            }
          } else if (randValue == 3) {
            if (bottom != null) {
              bottom ! PushSum(pushedS, pushedW, startTime)
              sent = true
            }
          } else if (randValue == 4) {
            if (random != null) {
              random ! PushSum(pushedS, pushedW, startTime)
              sent = true
            }
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