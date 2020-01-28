/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import edu.ie3.models.UniqueEntity;
import edu.ie3.models.result.system.PvResult;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Power;
import tec.uom.se.quantity.Quantities;

/**
 * Enum containing all {@link EntityFactory}s that can be used to create an instance of an {@link
 * UniqueEntity} based on {@link SimpleEntityData}
 *
 * @version 0.1
 * @since 28.01.20
 */
public enum SimpleEntityFactory implements EntityFactory<SimpleEntityFactory> {
  PvResultMapper(PvResult.class) {
    @Override
    public UniqueEntity getEntity(EntityData entityData) {

      Map<String, String> fieldsToAttributes =
          getSimpleEntityData(entityData).getFieldsToAttributes();

      // todo sanity check

      UUID uuid = UUID.fromString(fieldsToAttributes.get("uuid"));
      ZonedDateTime date = ZonedDateTime.parse(fieldsToAttributes.get("date"));
      UUID inputModel = UUID.fromString(fieldsToAttributes.get("uuid"));
      Quantity<Power> p =
          Quantities.getQuantity(
              Double.parseDouble(fieldsToAttributes.get("p")), PowerSystemUnits.WATT);
      Quantity<Power> q =
          Quantities.getQuantity(
              Double.parseDouble(fieldsToAttributes.get("q")), PowerSystemUnits.VAR);

      return new PvResult(uuid, date, inputModel, p, q);
    }
  };

  SimpleEntityFactory(Class<? extends UniqueEntity> clazz) {
    this.clazz = clazz;
  }

  private final Class<? extends UniqueEntity> clazz;

  @Override
  public Class<? extends UniqueEntity> clazz() {
    return clazz;
  }

  @Override
  public SimpleEntityFactory getRaw() {
    return this;
  }

  @Override
  public abstract UniqueEntity getEntity(EntityData metaData);

  private static SimpleEntityData getSimpleEntityData(EntityData entityData) {
    if (!(entityData instanceof SimpleEntityData)) {
      throw new RuntimeException("Invalid"); // todo
    } else {
      return (SimpleEntityData) entityData;
    }
  }
}
