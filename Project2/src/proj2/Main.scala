package proj2

import akka.actor.ActorSystem
import traits.Topology
import topologies.LineTopology
import akka.actor.ActorRef
import messages.PushSum
import messages.Gossip


object Main {

  def main(args: Array[String]) {
    val numNodes = args(0).toInt
    val topology = args(1)
    val algorithm = args(2)
    val system = ActorSystem("Project2")

    var projTopology: Topology = null

    if (topology == "line") {
      projTopology = new LineTopology(system, numNodes)
    } else if (topology == "2D") {

    } else if (topology == "full") {

    } else if (topology == "imp2D") {

    }

    val ref: ActorRef = projTopology.createTopology(numNodes)
    if (algorithm == "gossip") {
      ref ! Gossip
    } else if (algorithm == "push-sum") {
      ref ! PushSum(0, 0)
    }

  }
}