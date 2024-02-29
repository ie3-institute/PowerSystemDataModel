/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils

import edu.ie3.datamodel.exceptions.FailureException
import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.exceptions.TryException
import spock.lang.Specification

import java.util.stream.Stream

class TryTest extends Specification {

  def "A method can be applied to a try object"() {
    when:
    Try<String, Exception> actual = Try.of(() -> "success", Exception)

    then:
    actual.success
    actual.data.get() == "success"
    actual.orThrow == "success"
    actual.exception == Optional.empty()
  }

  def "A failing method can be applied to a try object"() {
    when:
    Try<Void, SourceException> actual = Try.of(() -> {
      throw new SourceException("Exception thrown.")
    }, SourceException)

    then:
    actual.failure
    actual.data == Optional.empty()
    actual.exception.get().class == SourceException
    actual.exception.get().message == "Exception thrown."
  }

  def "A failure is returned if an expected exception type is thrown when using #of()"() {
    when:
    def exception = new SourceException("source exception")
    Try<Void, SourceException> actual = Try.of(() -> {
      throw exception
    }, SourceException)

    then:
    actual.failure
    actual.exception.get() == exception
  }

  def "A TryException is thrown if an unexpected exception type is thrown when using #of()"() {
    when:
    Try.of(() -> {
      throw new SourceException("source exception")
    }, FailureException)

    then:
    TryException ex = thrown(TryException)
    ex.message == "Wrongly caught exception: "
    ex.cause.class == SourceException
    ex.cause.message == "source exception"
  }

  def "A failure is returned when using Failure#ofVoid() with an exception"() {
    when:
    def exception = new SourceException("source exception")
    Try<Void, SourceException> actual = Try.Failure.ofVoid(exception)

    then:
    actual.failure
    actual.exception.get() == exception
  }

  def "A failure is returned when using Failure#of() with an exception"() {
    when:
    def exception = new SourceException("source exception")
    Try<String, SourceException> actual = Try.Failure.of(exception)

    then:
    actual.failure
    actual.exception.get() == exception
  }

  def "A failure is returned if an expected exception type is thrown when using Try#ofVoid()"() {
    when:
    def exception = new SourceException("source exception")
    Try<Void, SourceException> actual = Try.ofVoid(() -> {
      throw exception
    }, SourceException)

    then:
    actual.failure
    actual.exception.get() == exception
  }

  def "A TryException is thrown if an unexpected exception type is thrown when using Try#ofVoid()"() {
    when:
    Try.ofVoid(() -> {
      throw new SourceException("source exception")
    }, FailureException)

    then:
    TryException ex = thrown(TryException)
    ex.message == "Wrongly caught exception: "
    ex.cause.class == SourceException
    ex.cause.message == "source exception"
  }

  def "A Try object can be creates by a boolean and an exception"() {
    when:
    def ex = new FailureException("failure")
    def actual = Try.ofVoid(bool, () -> ex)

    then:
    actual.failure == expected

    if (expected) {
      actual.exception.get() == ex
    }

    where:
    bool  || expected
    true  || true
    false || false
  }

  def "A list of Tries is returned when applying a multiple VoidSupplier to Try#ofVoid()"() {
    given:
    Try.VoidSupplier<FailureException> one = () -> {
      throw new FailureException("failure 1")
    }
    Try.VoidSupplier<FailureException> two = () -> {
      throw new FailureException("failure 2")
    }

    when:
    List<Try<Void, FailureException>> failures = Try.ofVoid(FailureException, one, two)

    then:
    failures.size() == 2
    failures.every {
      it.failure
    }
  }

  def "A TryException is thrown if an unexpected exception type is thrown when using Try#ofVoid() with multiple VoidSuppliers"() {
    given:
    Try.VoidSupplier<FailureException> one = () -> {
      throw new FailureException("failure")
    }
    Try.VoidSupplier<SourceException> two = () -> {
      throw new SourceException("source exception")
    }

    when:
    Try.ofVoid(FailureException, one, two)

    then:
    TryException ex = thrown(TryException)
    ex.cause.class == SourceException
    ex.cause.message == "source exception"
  }

  def "A Success is created from a non-empty Optional"() {
    given:
    def opt = Optional.of("Test")

    when:
    def actualTry = Try.from(opt, () -> new FailureException("failure"))

    then:
    actualTry.success
    actualTry.data.get() == "Test"
  }

  def "A Failure is created from an empty Optional"() {
    given:
    def opt = Optional.<String>empty()

    when:
    def actualTry = Try.from(opt, () -> new FailureException("failure"))

    then:
    actualTry.failure
    actualTry.exception.get().getMessage() == "failure"
  }

  def "A void method can be applied to a try object"() {
    when:
    Try<Void, Exception> actual = Try.ofVoid(() -> null, Exception)

    then:
    actual.success
    ((Try.Success<Void, Exception>) actual).empty
    actual.data.empty
  }

  def "A success object can be resolved with get method"() {
    given:
    Try<String, Exception> success = new Try.Success<>("success")

    when:
    String str = success.get()

    then:
    str == "success"
  }

  def "A failure object can be resolved with get method"() {
    given:
    Try<String, Exception> failure = new Try.Failure<>(new Exception("failure"))

    when:
    Exception ex = failure.get()

    then:
    ex.message == "failure"
  }

  def "An empty Success should work as expected"() {
    given:
    Try<Void, Exception> empty = Try.Success.empty() as Try<Void, Exception>

    expect:
    empty.success
    empty.data == Optional.empty()
  }

  def "A scan for exceptions should work as expected when failures are included"() {
    given:
    Set<Try<String, Exception>> set = Set.of(
    new Try.Success<>("one"),
    new Try.Failure<>(new Exception("exception")),
    new Try.Success<>("two"),
    new Try.Success<>("three")
    )

    when:
    Try<Set<String>, Exception> scan = Try.scanCollection(set, String)

    then:
    scan.failure
    scan.exception.get().message == "1 exception(s) occurred within \"String\" data, one is: java.lang.Exception: exception"
  }

  def "A scan for exceptions should work as expected when no failures are included"() {
    given:
    Set<Try<String, Exception>> set = Set.of(
    new Try.Success<>("one"),
    new Try.Success<>("two"),
    new Try.Success<>("three")
    )

    when:
    Try<Set<String>, Exception> scan = Try.scanCollection(set, String)

    then:
    scan.success
    scan.data.get().size() == 3
  }

  def "The getOrThrow method should work as expected on a success"() {
    given:
    def value = "some value"
    Try<String, SourceException> success = new Try.Success<>(value)

    when:
    def result = success.getOrThrow()

    then:
    noExceptionThrown()
    result == value
  }

  def "The getOrThrow method should work as expected on a failure"() {
    given:
    Try<String, SourceException> failure = new Try.Failure<>(new SourceException("source exception"))

    when:
    failure.getOrThrow()

    then:
    SourceException ex = thrown(SourceException)
    ex.message == "source exception"
  }

  def "A Try objects transformation should work as correctly for successes"() {
    given:
    Try<String, Exception> success = new Try.Success<>("5")
    SourceException exc = new SourceException("source exception")

    when:
    Try<Integer, Exception> transformS = success.transformS(str -> Integer.parseInt(str) )
    Try<Integer, Exception> map = success.map(str -> Integer.parseInt(str) )
    Try<String, Exception> transformF = success.transformF(ex -> new Exception(ex) )
    Try<Integer, Exception> transform = success.transform(str -> Integer.parseInt(str), ex -> new Exception(ex) )
    Try<Integer, Exception> flatMapS = success.flatMap(str -> new Try.Success(Integer.parseInt(str)) )
    Try<Integer, Exception> flatMapF = success.flatMap(str -> new Try.Failure(exc) )

    then:
    transformS.success
    map.success
    transformF.success
    transform.success
    flatMapS.success
    flatMapF.failure

    transformS.data.get() == 5
    map.data.get() == 5
    transformF.data.get() == "5"
    transform.data.get() == 5
    flatMapS.data.get() == 5
    flatMapF.exception.get() == exc
  }

  def "A Try objects transformation should work as correctly for failures"() {
    given:
    Try<String, Exception> failure = new Try.Failure<>(new SourceException(""))

    when:
    Try<Integer, Exception> transformS = failure.transformS(str -> Integer.parseInt(str) )
    Try<Integer, Exception> map = failure.map(str -> Integer.parseInt(str) )
    Try<String, Exception> transformF = failure.transformF(ex -> new Exception(ex) )
    Try<Integer, Exception> transform = failure.transform(str -> Integer.parseInt(str), ex -> new Exception(ex) )
    Try<Integer, Exception> flatMapS = failure.flatMap(str -> new Try.Success(Integer.parseInt(str)) )
    Try<Integer, Exception> flatMapF = failure.flatMap(str -> new Try.Failure(new SourceException("not returned")) )

    then:
    transformS.failure
    map.failure
    transformF.failure
    transform.failure
    flatMapS.failure
    flatMapF.failure

    transformS.exception.get().class == SourceException
    map.exception.get().class == SourceException
    transformF.exception.get().class == Exception
    transform.exception.get().class == Exception
    flatMapS.exception.get() == failure.get()
    flatMapF.exception.get() == failure.get()
  }

  def "The convert method should work correctly for successes"() {
    given:
    Try<Stream<Integer>, SourceException> success = new Try.Success(Stream.of(1, 2, 3))

    when:
    List<Try<Integer, SourceException>> converted = success.convert(d -> d.map(r -> Try.Success.of(r)).toList(), e -> [Try.Failure.of(e)] )

    then:
    converted.size() == 3
    converted.get(0).data.get() == 1
    converted.get(1).data.get() == 2
    converted.get(2).data.get() == 3
  }

  def "The convert method should work correctly for failures"() {
    given:
    SourceException exception = new SourceException("exception")
    Try<Stream<Integer>, SourceException> failure = new Try.Failure<>(exception)

    when:
    List<Try<Integer, SourceException>> converted = failure.convert(d -> d.map(r -> Try.Success.of(r)).toList(), e -> [Try.Failure.of(e)] )

    then:
    converted.size() == 1
    converted.get(0).exception.get() == exception
  }

  def "The getOrElse method should work as expected on a success"() {
    given:
    def value = "some value"
    Try<String, SourceException> success = new Try.Success<>(value)

    when:
    def result = success.getOrElse(() -> "other")

    then:
    result == value
  }

  def "The getOrElse method should work as expected on a failure"() {
    given:
    Try<String, SourceException> failure = new Try.Failure<>(new SourceException("source exception"))
    def defaultString = "other"

    when:
    def result = failure.getOrElse(() -> defaultString)

    then:
    result == defaultString
  }

  def "The orElse method should work as expected on a success"() {
    given:
    def otherSuccess = new Try.Success<>("other success")
    def otherFailure = new Try.Failure<>(new SourceException("other failure"))
    Try<String, SourceException> success = new Try.Success<>("some value")

    when:
    def result1 = success.orElse(() -> otherSuccess)
    def result2 = success.orElse(() -> otherFailure)

    then:
    result1 == success
    result2 == success
  }

  def "The orElse method should work as expected on a failure"() {
    given:
    def otherSuccess = new Try.Success<>("other success")
    def otherFailure = new Try.Failure<>(new SourceException("other failure"))
    Try<String, SourceException> failure = new Try.Failure<>(new SourceException("source exception"))

    when:
    def result1 = failure.orElse(() -> otherSuccess)
    def result2 = failure.orElse(() -> otherFailure)

    then:
    result1 == otherSuccess
    result2 == otherFailure
  }

  def "The toOptional method should work as expected on a success"() {
    given:
    def value = "some value"
    Try<String, SourceException> success = new Try.Success<>(value)

    when:
    def result = success.toOptional()

    then:
    result == Optional.of(value)
  }

  def "The toOptional method should work as expected on a failure"() {
    given:
    Try<String, SourceException> failure = new Try.Failure<>(new SourceException("some failure"))

    when:
    def result = failure.toOptional()

    then:
    result == Optional.empty()
  }

  def "The equals and hashCode method should work as expected on a success"() {
    given:
    def value = "some value"
    Try<String, SourceException> success1 = new Try.Success<>(value)
    Try<String, SourceException> success2 = new Try.Success<>(value)
    Try<String, SourceException> success3 = new Try.Success<>("other value")

    expect:
    success1 == success2
    success1.hashCode() == success2.hashCode()
    success1 != success3
    success1.hashCode() != success3.hashCode()
  }

  def "The equals and hashCode method should work as expected on a failure"() {
    given:
    // exceptions usually do not implement #equals, this is difficult to test
    Try<String, SourceException> failure1 = new Try.Failure<>(new SourceException("some failure"))
    Try<String, SourceException> failure2 = new Try.Failure<>(new SourceException("other failure"))

    expect:
    failure1 != failure2
    failure1.hashCode() != failure2.hashCode()
  }

  def "All exceptions of a collection of try objects should be returned"() {
    given:
    List<Try<String, Exception>> tries = List.of(
    new Try.Success<>("one"),
    new Try.Failure<>(new SourceException("source exception")),
    new Try.Failure<>(new UnsupportedOperationException("unsupported operation exception")),
    new Try.Success<>("two"),
    new Try.Failure<>(new SourceException("source exception 2"))
    )

    when:
    List<Exception> exceptions = Try.getExceptions(tries)

    then:
    exceptions.size() == 3

    exceptions.get(0).with {
      assert it.class == SourceException
      assert it.message == "source exception"
    }

    exceptions.get(1).with {
      assert it.class == UnsupportedOperationException
      assert it.message == "unsupported operation exception"
    }

    exceptions.get(2).with {
      assert it.class == SourceException
      assert it.message == "source exception 2"
    }
  }
}
