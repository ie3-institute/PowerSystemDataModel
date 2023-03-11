/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.options

import edu.ie3.datamodel.exceptions.SourceException
import spock.lang.Specification

import java.util.concurrent.Callable

class TryTest extends Specification {

  def "A method can be applied to a try object"() {
    when:
    Try<String, Exception> actual = Try.apply(TryTestData.success as Callable<String>)

    then:
    actual.success
    actual.data == "success"
  }

  def "A void method can be applied to a try object"() {
    when:
    Try<Void, Exception> actual = Try.apply(TryTestData.throwsException)

    then:
    actual.failure
    actual.exception.message == "Exception thrown."
  }

  def "A success object can be resolved with get method"() {
    given:
    Try<String, Exception> success = new Success<>("success")

    when:
    String str = success.get()

    then:
    str == "success"
    success.getData() == "success"
  }

  def "A failure object can be resolved with get method"() {
    given:
    Try<String, Exception> failure = new Failure<>(new Exception("failure"))

    when:
    String str = failure.get()

    then:
    Exception ex = thrown()
    str == null
    ex.message == "failure"
    failure.exception == ex
  }

  def "An empty Success should work as expected"() {
    given:
    Try<Void, Exception> empty = Success.empty()

    expect:
    empty.success
    empty.data == null
  }

  def "A scan for exceptions should work as expected"() {
    given:
    Set<Try<String, Exception>> set = Set.of(
        new Success<>("one"),
        new Failure<>(new Exception("exception")),
        new Success<>("two"),
        new Success<>("three")
        )

    when:
    Try<Set<String>, SourceException> scan = Try.scanForExceptions(set, String.class)

    then:
    scan.failure
    scan.exception.message == "1 exception(s) occurred within \"String\" data, one is: exception"
  }

  private class TryTestData {
    static Callable<String> success =  {return "success"}

    static Runnable throwsException = {throw new RuntimeException("Exception thrown.")}
  }
}
