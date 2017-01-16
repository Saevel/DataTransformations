package prv.zielony.data.transformations.core

import akka.actor.Address
import cats.data.Reader

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

package object services {

  implicit def liftFutureReader[X, Y](original: Reader[X, Y])(implicit executionContext: ExecutionContext): Reader[Future[X], Future[Y]] = {
    Reader[Future[X], Future[Y]]( futureX =>
      futureX.map(x => original.run(x))
    )
  }

  implicit def liftOptionReader[X, Y](original: Reader[X, Y]): Reader[Option[X], Option[Y]] = {
    Reader[Option[X], Option[Y]]( optionalX =>
      optionalX.map(x => original.run(x))
    )
  }

  implicit def liftTryReader[X, Y](original: Reader[X, Y]): Reader[Try[X], Try[Y]] = {
    Reader[Try[X], Try[Y]] ( tryX =>
      tryX.map(x => original.run(x))
    )
  }

  implicit def liftEitherReader[L, X, Y](original: Reader[X, Y]): Reader[Either[L, X], Either[L, Y]] = {
    Reader[Either[L, X], Either[L, Y]] { eitherXOrL =>
      eitherXOrL.map(x => original.run(x))
    }
  }

  implicit class AddressBuilder(context: StringContext) {
    def address( protocol: String, systemName: String, host: String, port: Int): Address =
      Address(protocol, systemName, host, port)
  }
}