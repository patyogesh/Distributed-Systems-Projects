package topologies

import akka.actor.ActorRef
import akka.actor.ActorSystem
import actors.LineActor
import akka.actor.Props
import traits.Topology
import messages.UpdateLineNeighbours


class LineTopology(system: ActorSystem, numNodes: Int) extends Topology {
	
  override def createTopology(numNodes: Int): ActorRef ={
    val actorRefArray: Array[ActorRef] = new Array[ActorRef](numNodes)
    for(i <- 1 to numNodes){
	  actorRefArray(i-1) = system.actorOf(Props(new LineActor(i, 1)), "LineActor"+i)
	}
    actorRefArray(0) ! UpdateLineNeighbours(null, actorRefArray(1))
    for(i <- 1 to numNodes-2){
      actorRefArray(i) ! UpdateLineNeighbours(actorRefArray(i-1), actorRefArray(i+1))
    }
    actorRefArray(numNodes-1) ! UpdateLineNeighbours(actorRefArray(numNodes-2), null)
    
    actorRefArray(0)
  }
}