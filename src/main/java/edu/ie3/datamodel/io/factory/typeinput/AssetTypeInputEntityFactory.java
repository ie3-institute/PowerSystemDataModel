/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput;

import edu.ie3.datamodel.io.factory.SimpleEntityFactory;
import edu.ie3.datamodel.models.input.AssetTypeInput;

/**
 * Internal API for building {@link AssetTypeInput}s. This additional abstraction layer is necessary
 * to create generic reader for {@link AssetTypeInput}s only and furthermore removes code
 * duplicates.
 *
 * @version 0.1
 * @since 11.02.20
 */
abstract class AssetTypeInputEntityFactory<T extends AssetTypeInput>
    extends SimpleEntityFactory<T> {

  protected static final String ENTITY_UUID = "uuid";
  protected static final String ENTITY_ID = "id";

  protected AssetTypeInputEntityFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
  }
}
