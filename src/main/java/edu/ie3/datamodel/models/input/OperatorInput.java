/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import java.util.Objects;
import java.util.UUID;

/** Describes an operator, that operates assets */
public class OperatorInput extends InputEntity {

  public static final OperatorInput NO_OPERATOR_ASSIGNED =
      new OperatorInput(UUID.randomUUID(), "NO_OPERATOR_ASSIGNED");

  /** The id (=name) of this operator */
  private final String id;

  /**
   * Constructor for an Operator
   *
   * @param uuid Unique identifier
   * @param id Human readable identifier
   */
  public OperatorInput(UUID uuid, String id) {
    super(uuid);
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public OperatorInputCopyBuilder copy() {
    return new OperatorInputCopyBuilder(this);
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
    return "OperatorInput{" + "uuid=" + getUuid() + ", id='" + id + '\'' + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link OperatorInput} entities with
   * altered field values. For detailed field descriptions refer to java docs of {@link
   * OperatorInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class OperatorInputCopyBuilder
      extends UniqueEntityCopyBuilder<OperatorInputCopyBuilder> {

    private String id;

    private OperatorInputCopyBuilder(OperatorInput entity) {
      super(entity);
      this.id = entity.getId();
    }

    @Override
    public OperatorInput build() {
      return new OperatorInput(getUuid(), id);
    }

    public OperatorInputCopyBuilder id(String id) {
      this.id = id;
      return this;
    }

    @Override
    protected OperatorInputCopyBuilder childInstance() {
      return this;
    }
  }
}
