/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.naming

import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel
import spock.lang.Specification

class ModelFieldsTest extends Specification {

  def "getMandatoryFields returns registered fields for MarkovLoadModel"() {
    when:
    def fields = ModelFields.getMandatoryFields(MarkovLoadModel)

    then:
    fields.size() == 1
    fields[0].containsAll([
      // top-level
      FieldNamingStrategy.MARKOV_SCHEMA,
      FieldNamingStrategy.MARKOV_GENERATED_AT,
      FieldNamingStrategy.MARKOV_GENERATOR,
      FieldNamingStrategy.MARKOV_TIME_MODEL,
      FieldNamingStrategy.MARKOV_VALUE_MODEL,
      FieldNamingStrategy.MARKOV_PARAMETERS,
      FieldNamingStrategy.MARKOV_DATA,
      // nested - required for simulation
      FieldNamingStrategy.MARKOV_GENERATOR_NAME,
      FieldNamingStrategy.MARKOV_GENERATOR_VERSION,
      FieldNamingStrategy.MARKOV_BUCKET_COUNT,
      FieldNamingStrategy.MARKOV_SAMPLING_INTERVAL,
      FieldNamingStrategy.MARKOV_TIMEZONE,
      FieldNamingStrategy.MARKOV_DISCRETIZATION_STATES,
      FieldNamingStrategy.MARKOV_DISCRETIZATION_THRESHOLDS,
      FieldNamingStrategy.MARKOV_MAX_POWER_VALUE,
      FieldNamingStrategy.MARKOV_MAX_POWER_UNIT,
      FieldNamingStrategy.MARKOV_MIN_POWER_VALUE,
      FieldNamingStrategy.MARKOV_MIN_POWER_UNIT,
      FieldNamingStrategy.MARKOV_TRANSITION_VALUES,
      FieldNamingStrategy.MARKOV_GMM_BUCKETS
    ])
  }
}
