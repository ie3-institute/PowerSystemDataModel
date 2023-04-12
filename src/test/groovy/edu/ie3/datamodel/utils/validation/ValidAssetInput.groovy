/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.models.input.AssetInput

import java.time.ZonedDateTime

class ValidAssetInput extends AssetInput {

  ValidAssetInput(String id) {
    super(UUID.randomUUID(), id)
  }

  @Override
  boolean inOperationOn(ZonedDateTime date) {
    throw new UnsupportedOperationException("This is a dummy class")
  }

  @Override
  UniqueEntityBuilder copy() {
    throw new UnsupportedOperationException("This is a dummy class")
  }
}
