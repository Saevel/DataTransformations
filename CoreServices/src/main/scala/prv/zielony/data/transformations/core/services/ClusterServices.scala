package prv.zielony.data.transformations.core.services

import akka.actor.{ActorRef, ActorSystem, Address}
import akka.cluster.Cluster
import akka.cluster.client.{ClusterClient, ClusterClientReceptionist, ClusterClientSettings}
import cats.data.Reader

import scala.collection.immutable.Seq

case class ClusterServiceSettings(systemName: String, seedNodes: Seq[Address])

case class ClusterEnvironment(system: ActorSystem, cluster: Cluster, client: ActorRef, receptionist: ClusterClientReceptionist)

trait ClusterServices {

  def joinCluster: Reader[ClusterServiceSettings, ClusterEnvironment] = Reader({ settings =>
    val actorSystem = ActorSystem(settings.systemName)
    val cluster = Cluster(actorSystem)
    cluster.joinSeedNodes(settings.seedNodes)
    val receptionist = new ClusterClientReceptionist(cluster.system)
    val client = cluster.system.actorOf(
      ClusterClient.props(ClusterClientSettings(cluster.system).withInitialContacts(Set(cluster.system / "receptionist"))),
      "clusterClient"
    )
    ClusterEnvironment(actorSystem, cluster, client, receptionist)
  })
}