/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import spock.lang.Specification

class TimeBasedWeatherValueDataTest extends Specification {
  def "equals returns true for identical objects"() {
    given:
    def fields = ["foo": "bar"]
    def point = new GeometryFactory().createPoint(new Coordinate(1, 2))
    def data1 = new TimeBasedWeatherValueData(fields, point)
    def data2 = new TimeBasedWeatherValueData(fields, point)

    expect:
    data1 == data2
  }

  def "equals returns false for different coordinates"() {
    given:
    def fields = ["foo": "bar"]
    def point1 = new GeometryFactory().createPoint(new Coordinate(1, 2))
    def point2 = new GeometryFactory().createPoint(new Coordinate(3, 4))
    def data1 = new TimeBasedWeatherValueData(fields, point1)
    def data2 = new TimeBasedWeatherValueData(fields, point2)

    expect:
    data1 != data2
  }

  def "hashCode returns same value for equal objects"() {
    given:
    def fields = ["foo": "bar"]
    def point = new GeometryFactory().createPoint(new Coordinate(1, 2))
    def data1 = new TimeBasedWeatherValueData(fields, point)
    def data2 = new TimeBasedWeatherValueData(fields, point)

    expect:
    data1.hashCode() == data2.hashCode()
  }

  def "toString contains key fields"() {
    given:
    def fields = ["foo": "bar"]
    def point = new GeometryFactory().createPoint(new Coordinate(1, 2))
    def data = new TimeBasedWeatherValueData(fields, point)

    expect:
    data.toString().contains("fieldsToAttributes")
    data.toString().contains("coordinate")
    data.toString().contains("foo")
    data.toString().contains("bar")
  }
}
