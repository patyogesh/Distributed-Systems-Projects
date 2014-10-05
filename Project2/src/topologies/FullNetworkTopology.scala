package topologies

import akka.actor.ActorSystem
import traits.Topology
import akka.actor.ActorRef
import akka.actor.Props
import actors.FullNetworkActor
import messages.UpdateFullNetworkActorNeighbours
import vo.ConvergenceCounter

class FullNetworkTopology(system: ActorSystem, numNodes: Int) extends Topology {

  override def createTopology(numNodes: Int, convergenceCounter: ConvergenceCounter): ActorRef ={
    convergenceCounter.actorsNotConvergedCounter = numNodes
    val actorRefArray: Array[ActorRef] = new Array[ActorRef](numNodes)
    for(i <- 1 to numNodes){
	  actorRefArray(i-1) = system.actorOf(Props(new FullNetworkActor(system, convergenceCounter, i, 1)), "FullNetworkActor"+i)
	}
    for(i <- 0 to numNodes-1){
      actorRefArray(i) ! UpdateFullNetworkActorNeighbours(actorRefArray, i)
    }
    
    actorRefArray(0)
  }
  
  override def failRandomNode(): Unit = {
    
  }
}