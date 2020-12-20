package advxml.core.data

import advxml.core.data.ValidateExTest.ContractFuncs
import advxml.testUtils.{ContractTests, FunSuiteContract}
import cats.data.{NonEmptyList, Validated}
import cats.data.Validated.Valid
import org.scalatest.funsuite.AnyFunSuite

import scala.util.{Failure, Success, Try}

class ValidateExTest extends AnyFunSuite with FunSuiteContract {

  import cats.instances.either._
  import cats.instances.option._
  import cats.instances.try_._

  // format: off
  ValidateExTest.Contract(
    f = ContractFuncs(
      toTry           = ValidatedNelEx.transform[Try, String](_),
      fromTry         = ValidatedNelEx.fromTry,
      toEitherEx      = ValidatedNelEx.transform[EitherEx, String](_),
      fromEitherEx    = ValidatedNelEx.fromEither,
      fromEitherNelEx = ValidatedNelEx.fromEitherNel,
      toOption        = ValidatedNelEx.transform[Option, String](_),
      fromOption      = (optionValue, ex) => ValidatedNelEx.fromOption(optionValue, ex)
    )
  ).runAll()
  // format: on
}

object ValidateExTest {

  case class ContractFuncs(
    toTry: ValidatedNelEx[String] => Try[String],
    fromTry: Try[String] => ValidatedNelEx[String],
    toEitherEx: ValidatedNelEx[String] => EitherEx[String],
    fromEitherEx: EitherEx[String] => ValidatedNelEx[String],
    fromEitherNelEx: EitherNelEx[String] => ValidatedNelEx[String],
    toOption: ValidatedNelEx[String] => Option[String],
    fromOption: (Option[String], Throwable) => ValidatedNelEx[String]
  )

  case class Contract(subDesc: String = "", f: ContractFuncs) extends ContractTests("ValidateEx", subDesc) {

    //Utils
    private val TEST_EXCEPTION = new RuntimeException("TEXT_EX")
    private val TEST_EXCEPTION_NEL = NonEmptyList.of(
      new RuntimeException("TEXT_EX_1"),
      new RuntimeException("TEXT_EX_2")
    )

    private def assertInvalid(v: ValidatedNelEx[_]): Unit =
      assert(v.isInvalid)

    private def assertValid[T](v: ValidatedNelEx[T], expectedValue: => T): Unit =
      assert(v == Valid(expectedValue))

    //############################ TO ###########################
    test("Valid.toTry") {
      val value = "TEST"
      val validatedExValue: ValidatedNelEx[String] = Validated.Valid(value)
      val result: Try[String] = f.toTry(validatedExValue)

      assert(result == Success(value))
    }

    test("Invalid.toTry") {
      val validatedExValue: ValidatedNelEx[String] = Validated.Invalid(TEST_EXCEPTION_NEL)
      val result: Try[String] = f.toTry(validatedExValue)

      assert(result.isFailure)
    }

    test("Valid.toEitherEx") {
      val value = "TEST"
      val validatedExValue: ValidatedNelEx[String] = Validated.Valid(value)
      val result: EitherEx[String] = f.toEitherEx(validatedExValue)

      assert(result == Right(value))
    }

    test("Invalid.toEitherEx") {
      val validatedExValue: ValidatedNelEx[String] = Validated.Invalid(TEST_EXCEPTION_NEL)
      val result: EitherEx[String] = f.toEitherEx(validatedExValue)

      assert(result.isLeft)
    }

    test("Valid.toOption") {
      val value = "TEST"
      val validatedExValue: ValidatedNelEx[String] = Validated.Valid(value)
      val result: Option[String] = f.toOption(validatedExValue)

      assert(result.contains(value))
    }

    test("Invalid.toOption") {
      val validatedExValue: ValidatedNelEx[String] = Validated.Invalid(TEST_EXCEPTION_NEL)
      val result: Option[String] = f.toOption(validatedExValue)

      assert(result.isEmpty)
    }

    //########################### FROM ##########################
    test("Try.Success.toValidatedEx") {
      val value = "TEST"
      val tryValue: Try[String] = Success(value)
      val validatedExValue: ValidatedNelEx[String] = f.fromTry(tryValue)

      assertValid(validatedExValue, value)
    }

    test("Try.Failure.toValidatedEx") {
      val tryValue: Try[String] = Failure(TEST_EXCEPTION)
      val validatedExValue: ValidatedNelEx[String] = f.fromTry(tryValue)

      assertInvalid(validatedExValue)
    }

    test("EitherEx.Right.toValidatedEx") {
      val value = "TEST"
      val eitherValue: EitherEx[String] = Right(value)
      val validatedExValue: ValidatedNelEx[String] = f.fromEitherEx(eitherValue)

      assertValid(validatedExValue, value)
    }

    test("EitherEx.Left.toValidatedEx") {
      val eitherValue: EitherEx[String] = Left(TEST_EXCEPTION)
      val validatedExValue: ValidatedNelEx[String] = f.fromEitherEx(eitherValue)

      assertInvalid(validatedExValue)
    }

    test("EitherNelEx.Right.toValidatedEx") {
      val value = "TEST"
      val eitherValue: EitherNelEx[String] = Right(value)
      val validatedExValue: ValidatedNelEx[String] = f.fromEitherNelEx(eitherValue)

      assertValid(validatedExValue, value)
    }

    test("EitherNelEx.Left.toValidatedEx") {
      val eitherValue: EitherNelEx[String] = Left(TEST_EXCEPTION_NEL)
      val validatedExValue: ValidatedNelEx[String] = f.fromEitherNelEx(eitherValue)

      assertInvalid(validatedExValue)
    }

    test("Option.Some.toValidatedEx") {
      val value = "TEST"
      val optionValue = Some(value)
      val validatedExValue: ValidatedNelEx[String] = f.fromOption(optionValue, TEST_EXCEPTION)

      assertValid(validatedExValue, value)
    }

    test("Option.None.toValidatedEx") {
      val optionValue: Option[String] = None
      val validatedExValue: ValidatedNelEx[String] = f.fromOption(optionValue, TEST_EXCEPTION)

      assertInvalid(validatedExValue)
    }
  }
}
