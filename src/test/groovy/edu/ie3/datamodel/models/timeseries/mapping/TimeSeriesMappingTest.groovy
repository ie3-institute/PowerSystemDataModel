/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.timeseries.mapping

import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.value.Value
import spock.lang.Shared
import spock.lang.Specification

class TimeSeriesMappingTest extends Specification {
	@Shared
	IndividualTimeSeries ts0, ts1, ts2, ts3

	def setupSpec() {
		ts0 = Mock(IndividualTimeSeries)
		ts0.uuid >> UUID.fromString("b09af80e-d65c-4339-b5af-5504339a3180")
		ts1 = Mock(IndividualTimeSeries)
		ts1.uuid >> UUID.fromString("ae675233-89ac-4323-b951-df406491f2f0")
		ts2 = Mock(IndividualTimeSeries)
		ts2.uuid >> UUID.fromString("9c26cc08-3c6e-4ce5-98c5-e8a6925e665c")
		ts3 = Mock(IndividualTimeSeries)
		ts3.uuid >> UUID.fromString("b8a7e2e7-6f66-4aa5-9c36-6b6bb323b37c")
	}

	def "The time series mapping is build correctly"() {
		given:
		def ts = [ts0, ts1, ts2, ts3]

		def entries = [
			new TimeSeriesMapping.Entry(UUID.randomUUID(), UUID.fromString("502351b5-21f1-489a-8ac0-b85893cbfa47"), ts0.uuid),
			new TimeSeriesMapping.Entry(UUID.randomUUID(), UUID.fromString("05d22f64-f252-4c4c-b724-dc69a0611ffe"), ts1.uuid),
			new TimeSeriesMapping.Entry(UUID.randomUUID(), UUID.fromString("43ca59dd-c70c-4184-9638-fd50da53847c"), ts2.uuid),
			new TimeSeriesMapping.Entry(UUID.randomUUID(), UUID.fromString("e6b6d460-e7ca-40ae-aa70-3f9e04ae52f0"), ts3.uuid)
		]

		def expectedMapping = [:]
		expectedMapping.put(UUID.fromString("502351b5-21f1-489a-8ac0-b85893cbfa47"), ts0)
		expectedMapping.put(UUID.fromString("05d22f64-f252-4c4c-b724-dc69a0611ffe"), ts1)
		expectedMapping.put(UUID.fromString("43ca59dd-c70c-4184-9638-fd50da53847c"), ts2)
		expectedMapping.put(UUID.fromString("e6b6d460-e7ca-40ae-aa70-3f9e04ae52f0"), ts3)

		when:
		def tsm = new TimeSeriesMapping(entries, ts)

		then:
		tsm.mapping == expectedMapping
	}


	def "The time series mapping throws an Exception, if a time series is missing"() {
		given:
		def ts = [ts0, ts1, ts2, ts3]

		def entries = [
			new TimeSeriesMapping.Entry(UUID.randomUUID(), UUID.fromString("502351b5-21f1-489a-8ac0-b85893cbfa47"), ts0.uuid),
			new TimeSeriesMapping.Entry(UUID.randomUUID(), UUID.fromString("05d22f64-f252-4c4c-b724-dc69a0611ffe"), UUID.fromString("ae675233-89ac-4323-b951-df406491f2f1")),
			new TimeSeriesMapping.Entry(UUID.randomUUID(), UUID.fromString("43ca59dd-c70c-4184-9638-fd50da53847c"), ts2.uuid),
			new TimeSeriesMapping.Entry(UUID.randomUUID(), UUID.fromString("e6b6d460-e7ca-40ae-aa70-3f9e04ae52f0"), ts3.uuid)
		]

		when:
		new TimeSeriesMapping(entries, ts)

		then:
		def ex = thrown(IllegalArgumentException)
		ex.message == "Cannot find referenced time series with uuid 'ae675233-89ac-4323-b951-df406491f2f1'."
	}

	def "The time series mapping returns correct entry on request"() {
		given:
		def mapping = [:]
		mapping.put(UUID.fromString("502351b5-21f1-489a-8ac0-b85893cbfa47"), ts0)
		mapping.put(UUID.fromString("05d22f64-f252-4c4c-b724-dc69a0611ffe"), ts1)
		mapping.put(UUID.fromString("43ca59dd-c70c-4184-9638-fd50da53847c"), ts2)
		mapping.put(UUID.fromString("e6b6d460-e7ca-40ae-aa70-3f9e04ae52f0"), ts3)
		def tsm = new TimeSeriesMapping(mapping)

		when:
		def actual = tsm.get(participant)

		then:
		actual.present
		actual.get() == expectedTs

		where:
		participant || expectedTs
		UUID.fromString("502351b5-21f1-489a-8ac0-b85893cbfa47") || ts0
		UUID.fromString("05d22f64-f252-4c4c-b724-dc69a0611ffe") || ts1
		UUID.fromString("43ca59dd-c70c-4184-9638-fd50da53847c") || ts2
		UUID.fromString("e6b6d460-e7ca-40ae-aa70-3f9e04ae52f0") || ts3
	}

	def "The time series mapping returns correct entries"() {
		given:
		def mapping = [:]
		mapping.put(UUID.fromString("502351b5-21f1-489a-8ac0-b85893cbfa47"), ts0)
		mapping.put(UUID.fromString("05d22f64-f252-4c4c-b724-dc69a0611ffe"), ts1)
		mapping.put(UUID.fromString("43ca59dd-c70c-4184-9638-fd50da53847c"), ts2)
		mapping.put(UUID.fromString("e6b6d460-e7ca-40ae-aa70-3f9e04ae52f0"), ts3)
		def tsm = new TimeSeriesMapping(mapping)

		def expectedEntries = [
			new TimeSeriesMapping.Entry(UUID.randomUUID(), UUID.fromString("e6b6d460-e7ca-40ae-aa70-3f9e04ae52f0"), ts3.uuid),
			new TimeSeriesMapping.Entry(UUID.randomUUID(), UUID.fromString("05d22f64-f252-4c4c-b724-dc69a0611ffe"), ts1.uuid),
			new TimeSeriesMapping.Entry(UUID.randomUUID(), UUID.fromString("43ca59dd-c70c-4184-9638-fd50da53847c"), ts2.uuid),
			new TimeSeriesMapping.Entry(UUID.randomUUID(), UUID.fromString("502351b5-21f1-489a-8ac0-b85893cbfa47"), ts0.uuid)
		]

		when:
		def actual = tsm.buildEntries().sort { a, b -> a.participant <=> b.participant }

		then:
		[actual, expectedEntries].transpose().forEach { it ->
			TimeSeriesMapping.Entry left = (it as ArrayList<TimeSeriesMapping.Entry>)[0]
			TimeSeriesMapping.Entry right = (it as ArrayList<TimeSeriesMapping.Entry>)[1]

			assert left.participant == right.participant
			assert left.timeSeries == right.timeSeries
		}
	}
}
