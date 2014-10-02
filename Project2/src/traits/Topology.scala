package traits

import akka.actor.ActorRef

trait Topology {

  def createTopology(numNodes: Int): ActorRef
}