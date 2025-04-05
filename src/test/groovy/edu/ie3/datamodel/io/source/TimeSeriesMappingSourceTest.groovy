/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import edu.ie3.datamodel.exceptions.SourceException
import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Stream

class TimeSeriesMappingSourceTest extends Specification {

  @Shared
  MixedMappingSource validSource

  def setupSpec() {
    validSource = new MixedMappingSource(true, true)
  }

  class MixedMappingSource extends TimeSeriesMappingSource {

    private boolean validAsset;
    private boolean validTimeSeries;

    MixedMappingSource(boolean validAsset, boolean validTimeSeries) {
      this.validAsset = validAsset;
      this.validTimeSeries = validTimeSeries;
    }

    @Override
    Stream<Map<String, String>> getMappingSourceData() throws SourceException {
      switch (getCase()){
        case "VALID":
          return Stream.of(
          Map.of("asset", "b86e95b0-e579-4a80-a534-37c7a470a409", "timeSeries", "9185b8c1-86ba-4a16-8dea-5ac898e8caa5")
          )
        case "INVALID_ASSET":
          return Stream.of(
          Map.of("asset", "invalidAsset", "timeSeries", "3fbfaa97-cff4-46d4-95ba-a95665e87c26"),
          Map.of("asset", "b86e95b0-e579-4a80-a534–37c7a470a409", "timeSeries", "9185b8c1–86ba– 4a16– 8dea– 5ac898e8caa5")
          )
        case "INVALID_TIMESERIES":
          return Stream.of(
          Map.of("asset", "90a96daa-012b-4fea-82dc-24ba7a7ab81c", "timeSeries", "invalidTimeSeries"),
          Map.of("asset","b86e95b0-e579– 4a80-a534–37c7a470a409","timeSeries","9185b8c1– 86ba– 4a16– 8dea– 5ac898e8caa5")
          )
        case "Invalid_ALL":
          return Stream.of(
          Map.of("asset", "invalidAsset", "timeSeries", "invalidTimeSeries"),
          )
        default:
          throw new SourceException("Invalid mapping configuration");
      }
    }

    private String getCase() {
      if (validAsset && validTimeSeries) {
        return "VALID";
      } else if (!validAsset && validTimeSeries) {
        return "INVALID_ASSET";
      } else if (validAsset && !validTimeSeries) {
        return "INVALID_TIMESERIES";
      } else if (!validAsset && !validTimeSeries) {
        return "INVALID_ALL";
      }
    }

    @Override
    Optional<Set<String>> getSourceFields() throws SourceException {
      return Optional.of(Set.of("asset", "timeSeries"))
    }
  }



  def "valid mapping entries should correctly be processed"(){
    given: "dummy mapping source with valid data"

    when:
    def actualMapping = validSource.getMapping()

    then:
    actualMapping.size() == 1
    actualMapping.containsKey(UUID.fromString("b86e95b0-e579-4a80-a534-37c7a470a409"))
    actualMapping.get(UUID.fromString("b86e95b0-e579-4a80-a534-37c7a470a409")) == UUID.fromString("9185b8c1-86ba-4a16-8dea-5ac898e8caa5")
  }

  def "should throw SourceException for invalid asset"(){
    given: "DummyMappingSource"
    validSource = new MixedMappingSource(false, true)
    when:
    validSource.getMapping()

    then:
    thrown(SourceException)
  }

  def "should throw SourceException for invalid timeSeries"(){
    given: "DummyMappingSource"
    validSource = new MixedMappingSource(true, false)
    when:
    validSource.getMapping()

    then:
    thrown(SourceException)
  }

  def "should throw SourceException for invalid timeSeries and asset"(){
    given: "DummyMappingSource"
    validSource = new MixedMappingSource(false, false)
    when:
    validSource.getMapping()

    then:
    thrown(SourceException)
  }
}
