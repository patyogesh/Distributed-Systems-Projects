package topologies

import akka.actor.ActorRef
import akka.actor.ActorSystem
import actors.LineActor
import akka.actor.Props
import traits.Topology
import messages.UpdateLineActorNeighbours
import vo.ConvergenceCounter

class LineTopology(system: ActorSystem, numNodes: Int) extends Topology {

  override def createTopology(numNodes: Int, convergenceCounter: ConvergenceCounter): ActorRef = {
    convergenceCounter.actorsNotConvergedCounter = numNodes
    val actorRefArray: Array[ActorRef] = new Array[ActorRef](numNodes)
    for (i <- 1 to numNodes) {
      actorRefArray(i - 1) = system.actorOf(Props(new LineActor(system, convergenceCounter, i, 1)), "LineActor" + i)
    }
    actorRefArray(0) ! UpdateLineActorNeighbours(null, actorRefArray(1))
    for (i <- 1 to numNodes - 2) {
      actorRefArray(i) ! UpdateLineActorNeighbours(actorRefArray(i - 1), actorRefArray(i + 1))
    }
    actorRefArray(numNodes - 1) ! UpdateLineActorNeighbours(actorRefArray(numNodes - 2), null)

    actorRefArray(0)
  }
  
  override def failRandomNode(): Unit = {
    
  }
}