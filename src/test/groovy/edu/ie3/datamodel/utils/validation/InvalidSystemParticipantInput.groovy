/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.SystemParticipantInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import tech.units.indriya.ComparableQuantity

import java.time.ZonedDateTime
import javax.measure.quantity.Power

class InvalidSystemParticipantInput extends SystemParticipantInput {
  InvalidSystemParticipantInput(NodeInput node) {
    super(UUID.randomUUID(), "invalid_system_participant", node, CosPhiFixed.CONSTANT_CHARACTERISTIC, null)
  }

  @Override
  boolean inOperationOn(ZonedDateTime date) {
    throw new UnsupportedOperationException("This is a dummy class")
  }

  @Override
  ComparableQuantity<Power> getsRated() {
    throw new UnsupportedOperationException("This is a dummy class")
  }

  @Override
  SystemParticipantInputCopyBuilder copy() {
    throw new UnsupportedOperationException("This is a dummy class")
  }
}
