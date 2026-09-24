/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import java.util.Map;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.UUID;

/** Describes an operator, that operates assets. */
public class OperatorInput extends UniqueInputEntity {
  public static final OperatorInput NO_OPERATOR_ASSIGNED =
      new OperatorInput(UUID.randomUUID(), "NO_OPERATOR_ASSIGNED");

  /** The id (=name) of this operator. */
  private final String id;

  /**
   * @param uuid Unique identifier for an entity.
   * @param id The id (=name) of this operator.
   */
  public OperatorInput(UUID uuid, String id) {
    super(uuid);
    this.id = id;
  }

  /**
   * @param uuid Unique identifier for an entity.
   * @param id The id (=name) of this operator.
   * @param additionalInformation Provided by the source.
   */
  public OperatorInput(UUID uuid, String id, Map<String, String> additionalInformation) {
    super(uuid);
    this.id = id;
    setAdditionalInformation(additionalInformation);
  }

  public String getId() {
    return id;
  }

  @Override
  public SequencedMap<String, String> toMap() {
    SequencedMap<String, String> map = super.toMap();
    map.put("id", id);
    map.putAll(getAdditionalInformation());
    return map;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof OperatorInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id);
  }

  @Override
  public String toString() {
    return "OperatorInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + id
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public OperatorInputCopyBuilder copy() {
    return new OperatorInputCopyBuilder(this);
  }

  public static class OperatorInputCopyBuilder
      extends UniqueInputEntityCopyBuilder<OperatorInputCopyBuilder> {
    private String id;

    protected OperatorInputCopyBuilder(OperatorInput entity) {
      super(entity);
      this.id = entity.id;
    }

    public OperatorInputCopyBuilder id(String id) {
      this.id = id;
      return thisInstance();
    }

    protected String getId() {
      return id;
    }

    @Override
    public OperatorInput build() {
      return new OperatorInput(getUuid(), id, getAdditionalInformation());
    }

    @Override
    protected OperatorInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
