/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput;

import edu.ie3.datamodel.io.factory.UniqueEntityFactory;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import java.util.Map;
import java.util.UUID;

/**
 * Internal API for building {@link AssetTypeInput}s. This additional abstraction layer is necessary
 * to create generic reader for {@link AssetTypeInput}s only and furthermore removes code
 * duplicates.
 */
abstract class AssetTypeInputEntityFactory<T extends AssetTypeInput, R extends AssetTypeInput>
    extends UniqueEntityFactory<T, R> {

  @SafeVarargs
  protected AssetTypeInputEntityFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
  }

  protected UUID getUUID(Map<String, String> data) {
    return getUUID(data, UUID);
  }

  protected String getID(Map<String, String> data) {
    return getField(data, ID);
  }
}
