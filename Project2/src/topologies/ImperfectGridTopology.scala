package topologies

import akka.actor.ActorSystem
import traits.Topology
import akka.actor.ActorRef
import akka.actor.Props
import messages.UpdateGridActorNeighbours
import messages.UpdateImperfectGridActorNeighbours
import actors.ImperfectGridActor
import vo.ConvergenceCounter

class ImperfectGridTopology(system: ActorSystem, numNodes: Int) extends Topology {

  var random = new scala.util.Random

  override def createTopology(numNodes: Int, convergenceCounter: ConvergenceCounter): ActorRef = {
    val gridDimension = Math.ceil(Math.sqrt(numNodes)).toInt
    convergenceCounter.actorsNotConvergedCounter = (gridDimension*gridDimension)
    val actorRefGrid: Array[Array[ActorRef]] = new Array[Array[ActorRef]](gridDimension)
    var counter = 1
    for (i <- 0 to gridDimension - 1) {
      actorRefGrid(i) = new Array[ActorRef](gridDimension)
      for (j <- 0 to gridDimension - 1) {
        actorRefGrid(i)(j) = system.actorOf(Props(new ImperfectGridActor(system, convergenceCounter, i, 1)), "ImperfectGridActor" + counter)
        counter += 1
      }
    }
    var top: ActorRef = null
    var left: ActorRef = null
    var right: ActorRef = null
    var bottom: ActorRef = null
    for (i <- 0 to gridDimension - 1) {
      for (j <- 0 to gridDimension - 1) {
        if (i == 0)
          top = null
        else
          top = actorRefGrid(i - 1)(j)
        if (j == 0)
          left = null
        else
          left = actorRefGrid(i)(j - 1)
        if (j == gridDimension - 1)
          right = null
        else
          right = actorRefGrid(i)(j + 1)
        if (i == gridDimension - 1)
          bottom = null
        else
          bottom = actorRefGrid(i + 1)(j)
        var randomActorRef: ActorRef = actorRefGrid(random.nextInt(gridDimension))(random.nextInt(gridDimension))
        actorRefGrid(i)(j) ! UpdateImperfectGridActorNeighbours(top, left, right, bottom, randomActorRef)
      }
    }
    actorRefGrid(0)(0)
  }
  
  override def failRandomNode(): Unit = {
    
  }
}