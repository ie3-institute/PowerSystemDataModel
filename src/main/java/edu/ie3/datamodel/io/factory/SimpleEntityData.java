/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory;

import edu.ie3.datamodel.models.UniqueEntity;
import java.util.Map;

/**
 * Data used by {@link SimpleEntityFactory} to create an instance of an entity than can be created
 * based only on a mapping of fieldName to value. This class can be used whenever no additional data
 * is needed, but should not be used as parent class for extensions. Use {@link EntityData} for
 * extensions instead.
 *
 * @version 0.1
 * @since 28.01.20
 */
public final class SimpleEntityData extends EntityData {

  public SimpleEntityData(
      Map<String, String> fieldsToAttributes, Class<? extends UniqueEntity> clazz) {
    super(fieldsToAttributes, clazz);
  }
}
