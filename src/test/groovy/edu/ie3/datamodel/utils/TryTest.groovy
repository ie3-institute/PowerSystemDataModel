/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils

import spock.lang.Specification

import java.util.concurrent.Callable

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
      throw new Exception("Exception thrown.")
    })

    then:
    actual.failure
    actual.exception().message == "Exception thrown."
  }

  def "A void method can be applied to a try object"() {
    when:
    Try<Void> actual = Try.testForException(() -> 1)

    then:
    actual.isSuccess()
    actual.isEmpty()
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
    Try<Set<String>> scan = Try.scanCollection(set, String.class)

    then:
    scan.failure
    scan.exception().message == "1 exception(s) occurred within \"String\" data, one is: exception"
  }
}
