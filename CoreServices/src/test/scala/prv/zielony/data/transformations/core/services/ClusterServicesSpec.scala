package prv.zielony.data.transformations.core.services

import akka.actor.{Actor, Props}
import akka.actor.Actor.Receive
import akka.pattern.ask
import akka.cluster.client.ClusterClient.Send
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, WordSpec}


/**
  * Created by Zielony on 2017-01-15.
  */
class ClusterServicesSpec extends WordSpec with Matchers with ClusterServices with ScalaFutures with IntegrationPatience {

  "ClusterServices" should {

    "expose services in a cluster" in {

      val nodeOne = joinCluster.map { env =>
        env.receptionist.registerService(env.system.actorOf(Props[PingActor], "pingActor"))
        env.cluster.registerOnMemberUp{
          val task = env.client ? Send(env.system / "pongActor" toString, Ping, true)
          task.futureValue should be(Pong)
        }
      }
      //TODO: Run with config1
      val nodeTwo = joinCluster.map { env =>
        env.receptionist.registerService(env.system.actorOf(Props[PongActor], "pongActor"))
        env.cluster.registerOnMemberUp{
          val task = env.client ? Send(env.system / "pingActor" toString, Pong, true)
          task.futureValue should be(Ping)
        }
      }

      

      //TODO: Run with config2
    }
  }

  private object Ping

  private object Pong

  private class PingActor extends Actor {
    override def receive: Receive = {
      case Pong => context.sender ! Ping
      case other => throw new IllegalArgumentException("Unexpected input in Ping Actor")
    }
  }

  private class PongActor extends Actor {
    override def receive: Receive = {
      case Ping => context.sender ! Pong
      case other => throw new IllegalArgumentException("Unexpected input in Pong Actor")
    }
  }
}