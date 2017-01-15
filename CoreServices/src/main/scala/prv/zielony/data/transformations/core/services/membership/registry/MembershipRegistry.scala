package prv.zielony.data.transformations.core.services.membership.registry

import akka.Done
import akka.actor.Address

import scala.collection.immutable.Seq
import scala.concurrent.Future

/**
  * Created by Zielony on 2017-01-15.
  */
trait MembershipRegistry {

  def registerJoined: Future[Done]

  def registerLeft: Future[Done]

  def getMembers: Future[Seq[Address]]
}
