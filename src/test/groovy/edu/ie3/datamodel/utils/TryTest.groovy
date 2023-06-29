/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils

import edu.ie3.datamodel.exceptions.RawGridException
import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.exceptions.TryException
import spock.lang.Specification

class TryTest extends Specification {

  def "A method can be applied to a try object"() {
    when:
    Try<String> actual = Try.of(() -> "success")

    then:
    actual.success
    actual.data() == "success"
  }

  def "A failing method can be applied to a try object"() {
    when:
    Try<Void> actual = Try.of(() -> {
      throw new SourceException("Exception thrown.")
    })

    then:
    actual.failure
    actual.exception().class == SourceException
    actual.exception().message == "Exception thrown."
  }

  def "A void method can be applied to a try object"() {
    when:
    Try<Void> actual = Try.testForException(() -> 1)

    then:
    actual.success
    actual.empty
    actual.data.empty
  }

  def "A success object can be resolved with get method"() {
    given:
    Try<String> success = new Try.Success<>("success")

    when:
    String str = success.get()

    then:
    str == "success"
    success.get() == "success"
  }

  def "A failure object can be resolved with get method"() {
    given:
    Try<String> failure = new Try.Failure<>(new Exception("failure"))

    when:
    Exception ex = failure.get()

    then:
    ex.message == "failure"
  }

  def "An empty Success should work as expected"() {
    given:
    Try<Void> empty = Try.Success.empty()

    expect:
    empty.success
    empty.data == Optional.empty()
    empty.empty
  }

  def "A scan for exceptions should work as expected"() {
    given:
    Set<Try<String>> set = Set.of(
    new Try.Success<>("one"),
    new Try.Failure<>(new Exception("exception")),
    new Try.Success<>("two"),
    new Try.Success<>("three")
    )

    when:
    Try<Set<String>> scan = Try.scanCollection(set, String)

    then:
    scan.failure
    scan.exception().message == "1 exception(s) occurred within \"String\" data, one is: exception"
  }

  def "The getOrThrow method should work as expected"() {
    given:
    Try<String> failure = new Try.Failure<>(new SourceException("source exception"))

    when:
    failure.getOrThrow()

    then:
    Exception ex = thrown()
    ex.class == TryException
    ex.cause.class == SourceException
    ex.cause.message == "source exception"
  }

  def "An exception thrown by a getOrThrow method can be cast to the specific exception class"() {
    given:
    Try<String> failure = new Try.Failure<>(new SourceException("source exception"))

    when:
    failure.getOrThrow(SourceException)

    then:
    SourceException ex = thrown()
    ex.message == "source exception"
  }

  def "An exception thrown by a getOrThrow method cannot be cast to a wrong exception class"() {
    given:
    Try<String> failure = new Try.Failure<>(new TryException())

    when:
    Try<String> empty = Try.of {
      failure.getOrThrow(RawGridException)
    }

    then:
    empty.failure
    empty.exception().class == ClassCastException
  }

  def "The getOrElse method should work as expected"() {
    given:
    Try<String> success = new Try.Success<>("success")
    Try<String> failure = new Try.Failure<>(new TryException())

    when:
    String successResult = success.getOrElse("else")
    String failureResult = failure.getOrElse("else")

    then:
    successResult == "success"
    failureResult == "else"
  }

  def "A Try objects transformation should work as correctly for successes"() {
    given:
    Try<String> numberString = new Try.Success<>("1")

    when:
    Try<Integer> first = numberString.transform( str -> Integer.parseInt(str) )
    Try<Integer> second = numberString.transform( str -> Integer.parseInt(str), ex -> new TryException(ex) ) as Try<Integer>

    then:
    first.success
    second.success

    first.data() == 1
    second.data() == 1
  }

  def "A Try objects transformation should work as correctly for failures"() {
    given:
    Try<String> failure = new Try.Failure<>(new TryException())

    when:
    Try<Integer> first = failure.transform( str -> Integer.parseInt(str) )
    Try<Integer> second = failure.transform( str -> Integer.parseInt(str), ex -> new Exception(ex) ) as Try<Integer>

    then:
    first.failure
    second.failure

    first.exception().class == TryException
    second.exception().class == Exception
  }

  def "All exceptions of a collection of try objects should be returned"() {
    given:
    List<Try<String>> tries = List.of(
    new Try.Success<>("one"),
    new Try.Failure<>(new SourceException("source exception")),
    new Try.Failure<>(new UnsupportedOperationException("unsupported operation exception")),
    new Try.Success<>("two"),
    new Try.Failure<>(new SourceException("source exception 2"))
    )

    when:
    List<? extends Exception> exceptions = Try.getExceptions(tries)

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
