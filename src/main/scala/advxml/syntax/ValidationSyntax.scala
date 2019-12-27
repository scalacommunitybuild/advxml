package advxml.syntax

import advxml.core.validate._
import cats.Alternative

import scala.util.Try

private[syntax] trait ValidationSyntax {

  implicit class ValidatedExTryOps[A](t: Try[A]) {
    def toValidatedEx: ValidatedEx[A] = ValidatedEx.fromTry(t)
  }

  implicit class ValidatedExEitherOps[A](e: EitherEx[A]) {
    def toValidatedEx: ValidatedEx[A] = ValidatedEx.fromEither(e)
  }

  implicit class ValidatedExEitherNelOps[A](e: EitherNelEx[A]) {
    def toValidatedEx: ValidatedEx[A] = ValidatedEx.fromEitherNel(e)
  }

  implicit class ValidatedExOptionOps[A](e: Option[A]) {
    def toValidatedEx(ifNone: => Throwable): ValidatedEx[A] = ValidatedEx.fromOption(e, ifNone)
  }

  implicit class ValidatedExOps[A](validated: ValidatedEx[A]) {

    def transformE[F[_]](implicit F: MonadEx[F]): F[A] =
      ValidatedEx.transformE[F, A](validated)(F)

    def transformNE[F[_]](implicit F: MonadNelEx[F]): F[A] =
      ValidatedEx.transformNE[F, A](validated)(F)

    def transformA[F[_]](implicit F: Alternative[F]): F[A] =
      ValidatedEx.transformA[F, A](validated)(F)
  }
}
