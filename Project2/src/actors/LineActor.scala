package actors

import akka.actor.Actor
import akka.actor.ActorRef
import messages.UpdateLineNeighbours
import messages.PushSum
import messages.Gossip
import messages.UpdateLineNeighbours
import messages.PushSum

class LineActor(s: BigDecimal, w: BigDecimal) extends Actor {

  val NumberOfGossips = 4

  var prev: ActorRef = null
  var next: ActorRef = null
  var localS: BigDecimal = s
  var localW: BigDecimal = w
  var last3: Array[BigDecimal] = Array(-1, -1, -1)
  private var count: Int = 0
  var random = new scala.util.Random
  var isGossiping = false

  def receive() = {
    case UpdateLineNeighbours(prev, next) =>
      this.prev = prev
      this.next = next
    case Gossip =>
      if (count < NumberOfGossips) {
        count = count + 1
        println("Gossiping " + count)
        Thread sleep 1000

        var sent = false
        while (!sent) {
          if (random.nextInt % 2 == 0) {
            if (prev != null) {
              prev ! Gossip
              sent = true
            } else {
              next ! Gossip
              sent = true
            }
          } else {
            if (next != null) {
              next ! Gossip
              sent = true
            } else {
              prev ! Gossip
              sent = true
            }
          }
        }
      }
    case PushSum(s, w) =>
      if (last3(0) >= 0.0000000001 || last3(1) >= 0.0000000001 || last3(2) >= 0.0000000001 || 
          last3(0) == -1) {
        println("Pushing")
        this.localS += s
        this.localW += w
        var sent = false
        var oldSByW = localS /localW 
        val pushedS = localS / 2
        localS -= localS / 2
        val pushedW = localW / 2
        localW -= localW / 2
        while (!sent) {
          if (random.nextInt % 2 == 0) {
            if (prev != null) {
              prev ! PushSum(pushedS, pushedW)
              sent = true
            } else {
              next ! PushSum(pushedS, pushedW)
              sent = true
            }
          } else {
            if (next != null) {
              next ! PushSum(pushedS, pushedW)
              sent = true
            } else {
              prev ! PushSum(pushedS, pushedW)
              sent = true
            }
          }
        }
        var newSByW = localS /localW 
        last3(0) = last3(1)
        last3(1) = last3(2)
        last3(2) = newSByW - oldSByW
        println("Pushing sum : " + pushedS + "/" + pushedW)
      }
    case _ => //
  }
}