package prv.zielony.data.transformations.core.services.monitoring

import akka.Done

import scala.concurrent.Future

/**
  * Created by Zielony on 2017-01-14.
  */
trait ClusterMonitoring {

  //TODO: Monitoring interface

  def debug(message: String): Unit

  def info(message: String): Unit

  def warn(message: String): Unit

  def error(message: String): Unit

}
