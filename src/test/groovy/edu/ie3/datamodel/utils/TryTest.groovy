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

class TryTest extends Specification {

  def "A method can be applied to a try object"() {
    when:
    Try<String, Exception> actual = Try.of(() -> "success", Exception)

    then:
    actual.success
    actual.data() == "success"
  }

  def "A failing method can be applied to a try object"() {
    when:
    Try<Void, SourceException> actual = Try.of(() -> {
      throw new SourceException("Exception thrown.")
    }, SourceException)

    then:
    actual.failure
    actual.exception().class == SourceException
    actual.exception().message == "Exception thrown."
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
    Exception ex = thrown()
    ex.class == TryException
    ex.message == "Wrongly caught exception: "
    Throwable cause = ex.cause
    cause.class == SourceException
    cause.message == "source exception"
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

  def "A failure is returned when using Failure#of() with a failure"() {
    when:
    def exception = new SourceException("source exception")
    Try<Void, SourceException> actual = Try.Failure.of(new Try.Failure<String,SourceException>(exception))

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
    Exception ex = thrown()
    ex.class == TryException
    ex.message == "Wrongly caught exception: "
    Throwable cause = ex.cause
    cause.class == SourceException
    cause.message == "source exception"
  }

  def "A Try object can be creates by a boolean and an exception"() {
    when:
    def actual = Try.ofVoid(bool, ex)

    then:
    actual.failure == expected

    if (expected) {
      actual.exception() == ex
    }

    where:
    bool | ex | expected
    true | new FailureException("failure") | true
    false | new FailureException("no failure") | false
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
    failures.forEach {
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
    Exception ex = thrown()
    ex.class == TryException
    Throwable cause = ex.cause
    cause.class == SourceException
    cause.message == "source exception"
  }

  def "A void method can be applied to a try object"() {
    when:
    Try<Void, Exception> actual = Try.ofVoid(() -> null, Exception)

    then:
    actual.success
    actual.empty
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
    empty.empty
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
    scan.exception().message == "1 exception(s) occurred within \"String\" data, one is: java.lang.Exception: exception"
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
    scan.data().size() == 3
  }

  def "The getOrThrow method should work as expected"() {
    given:
    Try<String, SourceException> failure = new Try.Failure<>(new SourceException("source exception"))

    when:
    failure.orThrow

    then:
    Exception ex = thrown()
    ex.class == SourceException
    ex.message == "source exception"
  }

  def "The getOrElse method should work as expected"() {
    given:
    Try<String, Exception> success = new Try.Success<>("success")
    Try<String, SourceException> failure = new Try.Failure<>(new SourceException("exception"))

    when:
    String successResult = success.getOrElse("else")
    String failureResult = failure.getOrElse("else")

    then:
    successResult == "success"
    failureResult == "else"
  }

  def "A Try objects transformation should work as correctly for successes"() {
    given:
    Try<String, Exception> success = new Try.Success<>("5")

    when:
    Try<Integer, Exception> first = success.transformS(str -> Integer.parseInt(str) )
    Try<Integer, Exception> second = success.transform(str -> Integer.parseInt(str), ex -> new Exception(ex) )

    then:
    first.success
    second.success

    first.data() == 5
    second.data() == 5
  }

  def "A Try objects transformation should work as correctly for failures"() {
    given:
    Try<String, Exception> failure = new Try.Failure<>(new SourceException(""))

    when:
    Try<Integer, Exception> first = failure.transformS(str -> Integer.parseInt(str) )
    Try<Integer, Exception> second = failure.transform(str -> Integer.parseInt(str), ex -> new Exception(ex) )

    then:
    first.failure
    second.failure

    first.exception().class == SourceException
    second.exception().class == Exception
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
