package proj2

import akka.actor.ActorSystem
import traits.Topology
import topologies.LineTopology
import akka.actor.ActorRef
import topologies.GridTopology
import topologies.FullNetworkTopology
import topologies.ImperfectGridTopology
import messages.GossipIn
import vo.ConvergenceCounter
import vo.ConvergenceCounter
import messages.PushSumIn

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
      projTopology = new GridTopology(system, numNodes)
    } else if (topology == "full") {
      projTopology = new FullNetworkTopology(system, numNodes)
    } else if (topology == "imp2D") {
      projTopology = new ImperfectGridTopology(system, numNodes)
    }

    var convergenceCounter: ConvergenceCounter = new ConvergenceCounter
    val ref: ActorRef = projTopology.createTopology(numNodes, convergenceCounter)
    var startTime: Long = -1
    if (algorithm == "gossip") {
      startTime = System.currentTimeMillis()
      ref ! GossipIn(startTime)
    } else if (algorithm == "push-sum") {
      startTime = System.currentTimeMillis()
      ref ! PushSumIn(0, 0, startTime)
    }

  }
}