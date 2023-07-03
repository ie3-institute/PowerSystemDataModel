/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils

import edu.ie3.datamodel.exceptions.SourceException
import spock.lang.Specification

class TryTest extends Specification {

  def "A method can be applied to a try object"() {
    when:
    Try<String, Exception> actual = Try.of(() -> "success")

    then:
    actual.success
    actual.data() == "success"
  }

  def "A failing method can be applied to a try object"() {
    when:
    Try<Void, SourceException> actual = Try.of(() -> {
      throw new SourceException("Exception thrown.")
    })

    then:
    actual.failure
    actual.exception().class == SourceException
    actual.exception().message == "Exception thrown."
  }

  def "A void method can be applied to a try object"() {
    when:
    Try<Void, Exception> actual = Try.ofVoid(() -> null)

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
    success.get() == "success"
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

  def "A scan for exceptions should work as expected"() {
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
    scan.exception().message == "1 exception(s) occurred within \"String\" data, one is: exception"
  }

  def "The getOrThrow method should work as expected"() {
    given:
    Try<String, SourceException> failure = new Try.Failure<>(new SourceException("source exception"))

    when:
    failure.getOrThrow()

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

  def "A Try objects transformation should work as correctly for failures"() {
    given:
    Try<String, Exception> failure = new Try.Failure<>(new SourceException(""))

    when:
    Try<Integer, Exception> first = failure.transform( str -> Integer.parseInt(str) )
    Try<Integer, Exception> second = failure.transform( str -> Integer.parseInt(str), ex -> new Exception(ex) )

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
