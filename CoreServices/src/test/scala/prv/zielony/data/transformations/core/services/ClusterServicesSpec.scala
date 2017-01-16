package prv.zielony.data.transformations.core.services

import akka.actor.{Actor, Props}
import akka.actor.Actor.Receive
import akka.pattern.ask
import akka.cluster.client.ClusterClient.Send
import akka.util.Timeout
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, WordSpec}

import scala.collection.immutable.Seq
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Zielony on 2017-01-15.
  */
class ClusterServicesSpec extends WordSpec with Matchers with ClusterServices with ScalaFutures with IntegrationPatience {

  //TODO: Generalize

  "ClusterServices" should {

    "expose services in a cluster" in {

      implicit val timeout: Timeout = 30 seconds

      val systemName = "ClusterSystem"

      val firstNodeAddress = address"${"akka.tcp"}://${systemName}@${"127.0.0.1"}:${5575}"
      val secondNodeAddress = address"${"akka.tcp"}://${systemName}@${"127.0.0.1"}:${5576}"

      val nodeOneConfig = ClusterServiceSettings(
        systemName,
        Seq(firstNodeAddress),
        firstNodeAddress.port.get
      )

      val nodeTwoConfig = ClusterServiceSettings(
        systemName,
        Seq(firstNodeAddress),
        secondNodeAddress.port.get
      )

      val nodeOneTask = joinCluster.map { env =>
        env.receptionist.registerService(env.system.actorOf(Props(new PingActor), "pingActor"))
        env.cluster.registerOnMemberUp {
          val task = env.client ? Send(env.system / "pongActor" toString, Ping, true)
          task.futureValue
        }
      }

      val nodeTwoTask = joinCluster.map { env =>
        env.receptionist.registerService(env.system.actorOf(Props( new PongActor), "pongActor"))
        env.cluster.registerOnMemberUp{
          val task = env.client ? Send(env.system / "pingActor" toString, Pong, true)
          task.futureValue
        }
      }

      val nodeOne = Future {
        nodeOneTask.run(nodeOneConfig)
      }

      val nodeTwo = Future {
        nodeTwoTask.run(nodeTwoConfig)
      }

      nodeOne.futureValue should be(Pong)
      nodeTwo.futureValue should be(Ping)
    }
  }

  object Ping

  object Pong

  class PingActor() extends Actor {
    override def receive: Receive = {
      case Pong => context.sender ! Ping
      case other => throw new IllegalArgumentException("Unexpected input in Ping Actor")
    }
  }

  class PongActor() extends Actor {
    override def receive: Receive = {
      case Ping => context.sender ! Pong
      case other => throw new IllegalArgumentException("Unexpected input in Pong Actor")
    }
  }
}