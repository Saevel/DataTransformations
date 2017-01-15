package prv.zielony.data.transformations.core.services

import scala.annotation.tailrec
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

/**
  * Created by Zielony on 2017-01-15.
  */
package object retryable {

  trait RetryStrategy {
    def shouldRetry: Boolean
  }

  class TimeoutRetryStrategy(timeout: Duration) extends RetryStrategy {

    private var firstTime: Boolean = false

    private var startTime: Long = ???

    override def shouldRetry: Boolean = if(firstTime && timeout.toNanos > 0) {
      startTime = System.nanoTime
      firstTime = false
      true
    } else if(System.nanoTime - startTime < timeout.toNanos) true
      else false
  }

  object InfiniteRetryStrategy extends TimeoutRetryStrategy(Duration.Inf)

  class CountingRetryStrategy(maxCount: Int) extends RetryStrategy {

    private var count = 0;

    override def shouldRetry: Boolean = if(count < maxCount) {
      count = count + 1
      true
    } else false
  }

  def retry[T](strategy: RetryStrategy)(f: => T)(implicit executionContext: ExecutionContext): Future[T] = {
    Future(f).transformWith[T]{
      case Success(result) => Future.successful(result)
      case Failure(NonFatal(e)) => if(strategy.shouldRetry) retry(strategy)(f) else Future.failed[T](e)
    }
  }
}