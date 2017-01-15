package prv.zielony.data.transformations.core.services.membership.registry

import akka.Done
import akka.actor.Address

import scala.collection.immutable.Seq
import scala.concurrent.Future

/**
  * Created by Zielony on 2017-01-15.
  */
object RedisMembershipRegistry extends MembershipRegistry {

  override def registerJoined: Future[Done] = ???

  override def getMembers: Future[Seq[Address]] = ???

  override def registerLeft: Future[Done] = ???
}
