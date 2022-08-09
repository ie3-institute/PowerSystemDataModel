/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.system.type.SystemParticipantTypeInput
import tech.units.indriya.quantity.Quantities

class InvalidSystemParticipantTypeInput extends SystemParticipantTypeInput {
  /**
   * @param uuid of the input entity
   * @param id of this type of system participant
   * @param capex Captial expense for this type of system participant (typically in €)
   * @param opex Operating expense for this type of system participant (typically in €/MWh)
   * @param sRated Rated apparent power
   * @param cosPhiRated Power factor for this type of system participant
   */
  InvalidSystemParticipantTypeInput() {
    super(UUID.randomUUID(), "invalid_system_participant_type", Quantities.getQuantity(0d, StandardUnits.CAPEX), Quantities.getQuantity(0d, StandardUnits.ENERGY_PRICE), Quantities.getQuantity(0d, StandardUnits.S_RATED), 1.0d)
  }
}
