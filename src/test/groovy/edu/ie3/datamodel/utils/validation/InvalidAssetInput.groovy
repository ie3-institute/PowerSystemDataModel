/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.models.input.AssetInput

import java.time.ZonedDateTime

class InvalidAssetInput  extends AssetInput {

  InvalidAssetInput() {
    super(UUID.randomUUID(), "invalid_asset")
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
