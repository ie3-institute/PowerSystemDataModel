/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.models.input.AssetTypeInput

class InvalidAssetTypeInput extends AssetTypeInput {
  InvalidAssetTypeInput(UUID uuid, String id) {
    super(uuid, id)
  }

  AssetTypeInputCopyBuilder copy() {
    return null
  }

  InvalidAssetTypeInput() {
    super(UUID.randomUUID(), "invalid_asset_type")
  }
}