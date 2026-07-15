/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import edu.ie3.datamodel.models.UniqueEntity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Functionless class to describe that all subclasses are unique input classes */
public abstract class UniqueInputEntity extends UniqueEntity implements InputEntity {

  private final Map<String, String> additionalInformation = new HashMap<>();

  protected UniqueInputEntity(UUID uuid) {
    super(uuid);
  }

  /**
   * Method for adding additional information.
   *
   * @param additionalInformation That were provided by the source
   */
  protected void setAdditionalInformation(Map<String, String> additionalInformation) {
    this.additionalInformation.putAll(additionalInformation);
  }

  @Override
  public Map<String, String> getAdditionalInformation() {
    return Collections.unmodifiableMap(additionalInformation);
  }

  /**
   * Abstract class for all copy builders that build child input entities.
   *
   * @param <B> concrete copy builder type
   */
  public abstract static class UniqueInputEntityCopyBuilder<
          B extends UniqueInputEntityCopyBuilder<B>>
      extends UniqueEntityCopyBuilder<B> {

    private Map<String, String> additionalInformation;

    protected UniqueInputEntityCopyBuilder(UniqueInputEntity entity) {
      super(entity);
      this.additionalInformation = new HashMap<>(entity.getAdditionalInformation());
    }

    /**
     * Replace the additional information of the copied entity.
     *
     * @param additionalInformation additional information for the copied entity
     * @return this copy builder
     */
    public B additionalInformation(Map<String, String> additionalInformation) {
      this.additionalInformation = new HashMap<>(additionalInformation);
      return thisInstance();
    }

    protected Map<String, String> getAdditionalInformation() {
      return Collections.unmodifiableMap(additionalInformation);
    }
  }
}
