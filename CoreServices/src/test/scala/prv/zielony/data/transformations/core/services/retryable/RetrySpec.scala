package prv.zielony.data.transformations.core.services.retryable

import org.scalacheck.Gen
import org.scalatest.{Matchers, PropSpec}
import org.scalatest.concurrent.ScalaFutures
import org.scalacheck.Prop._
import org.scalatest.prop.PropertyChecks

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by Zielony on 2017-01-15.
  */
class RetrySpec extends PropSpec with PropertyChecks with Matchers with ScalaFutures {

  private val nonNegativeSmallInts = Gen.choose(0, 10).map(i => if(i < 0) (-1)*i else i)

  private val sampleException = new IllegalArgumentException("Planned throw")

  property("Retry should retry until advised so by the strategy") {

    forAll(nonNegativeSmallInts){ retries =>

      var count = 0
      def countAndTrow: Any = {
        count = count + 1
        throw sampleException
      }

      val task = retry(new CountingRetryStrategy(retries))(countAndTrow)

      Await.ready(task, (1 + retries) seconds)

      count should be(1 + retries)
      task.failed.futureValue should be(sampleException)
    }
  }

  property("Retry should not retry a successful future") {
    forAll(nonNegativeSmallInts){ retries =>

      var count = 0
      val sampleValue = 123
      def returnAnything: Any = {
        count = count + 1
        sampleValue
      }

      val task = retry(new CountingRetryStrategy(retries))(returnAnything)

      val result = Await.result(task, (1 + retries) seconds)

      count should be(1)
      result should be(sampleValue)
    }
  }
}