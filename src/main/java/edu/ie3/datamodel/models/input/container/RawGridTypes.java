/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.connector.type.CableTypeInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import java.util.*;
import java.util.stream.Collectors;

/** Represents the aggregation of raw grid elements (nodes, lines, transformers, switches) */
public class RawGridTypes implements InputContainer<AssetTypeInput> {
  /** Set of line types in this grid */
  private final Set<LineTypeInput> lineTypes;

  /** Set of cable types in this grid */
  private final Set<CableTypeInput> cableTypes;

  /** Set of two winding transformers types in this grid */
  private final Set<Transformer2WTypeInput> transformer2WTypes;

  /** Set of three winding types in this grid */
  private final Set<Transformer3WTypeInput> transformer3WTypes;

  public RawGridTypes(
      Set<LineTypeInput> lineTypes,
      Set<CableTypeInput> cableTypes,
      Set<Transformer2WTypeInput> transformer2WTypes,
      Set<Transformer3WTypeInput> transformer3WTypes) {
    this.lineTypes = lineTypes;
    this.cableTypes = cableTypes;
    this.transformer2WTypes = transformer2WTypes;
    this.transformer3WTypes = transformer3WTypes;
  }

  /**
   * Combine different already existing containers
   *
   * @param rawGridTypes Already existing containers
   */
  public RawGridTypes(Collection<RawGridTypes> rawGridTypes) {
    this.lineTypes =
        rawGridTypes.stream()
            .flatMap(rawElements -> rawElements.getLineTypes().stream())
            .collect(Collectors.toSet());
    this.cableTypes =
        rawGridTypes.stream()
            .flatMap(rawElements -> rawElements.getCableTypes().stream())
            .collect(Collectors.toSet());
    this.transformer2WTypes =
        rawGridTypes.stream()
            .flatMap(rawElements -> rawElements.getTransformer2WTypes().stream())
            .collect(Collectors.toSet());
    this.transformer3WTypes =
        rawGridTypes.stream()
            .flatMap(rawElements -> rawElements.getTransformer3WTypes().stream())
            .collect(Collectors.toSet());
  }

  /**
   * Create an instance based on a list of {@link AssetTypeInput} entities that are included in
   * {@link RawGridTypes}
   *
   * @param rawGridTypes list of type elements this container instance should create from
   */
  public RawGridTypes(List<AssetTypeInput> rawGridTypes) {

    /* init sets */
    this.lineTypes =
        rawGridTypes.parallelStream()
            .filter(LineTypeInput.class::isInstance)
            .map(LineTypeInput.class::cast)
            .collect(Collectors.toSet());
    this.cableTypes =
        rawGridTypes.parallelStream()
            .filter(CableTypeInput.class::isInstance)
            .map(CableTypeInput.class::cast)
            .collect(Collectors.toSet());
    this.transformer2WTypes =
        rawGridTypes.parallelStream()
            .filter(Transformer2WTypeInput.class::isInstance)
            .map(Transformer2WTypeInput.class::cast)
            .collect(Collectors.toSet());
    this.transformer3WTypes =
        rawGridTypes.parallelStream()
            .filter(Transformer3WTypeInput.class::isInstance)
            .map(Transformer3WTypeInput.class::cast)
            .collect(Collectors.toSet());
  }

  @Override
  public final List<AssetTypeInput> allEntitiesAsList() {
    List<AssetTypeInput> allEntities = new ArrayList<>();
    allEntities.addAll(lineTypes);
    allEntities.addAll(cableTypes);
    allEntities.addAll(transformer2WTypes);
    allEntities.addAll(transformer3WTypes);
    return Collections.unmodifiableList(allEntities);
  }

  @Override
  public RawGridTypesCopyBuilder copy() {
    return new RawGridTypesCopyBuilder(this);
  }

  /**
   * @return unmodifiable Set of all line types in this grid
   */
  public Set<LineTypeInput> getLineTypes() {
    return Collections.unmodifiableSet(lineTypes);
  }

  /**
   * @return unmodifiable Set of all cable types in this grid
   */
  public Set<CableTypeInput> getCableTypes() {
    return Collections.unmodifiableSet(cableTypes);
  }

  /**
   * @return unmodifiable Set of all two winding transformers in this grid
   */
  public Set<Transformer2WTypeInput> getTransformer2WTypes() {
    return Collections.unmodifiableSet(transformer2WTypes);
  }

  /**
   * @return unmodifiable Set of all three winding transformers in this grid
   */
  public Set<Transformer3WTypeInput> getTransformer3WTypes() {
    return Collections.unmodifiableSet(transformer3WTypes);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RawGridTypes that)) return false;
    return lineTypes.equals(that.lineTypes)
        && cableTypes.equals(that.cableTypes)
        && transformer2WTypes.equals(that.transformer2WTypes)
        && transformer3WTypes.equals(that.transformer3WTypes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lineTypes, cableTypes, transformer2WTypes, transformer3WTypes);
  }

  /**
   * A builder pattern based approach to create copies of {@link RawGridTypes} containers with
   * altered field values. For detailed field descriptions refer to Javadocs of {@link RawGridTypes}
   */
  public static class RawGridTypesCopyBuilder extends InputContainerCopyBuilder<AssetTypeInput> {
    private Set<LineTypeInput> lineTypes;
    private Set<CableTypeInput> cableTypes;
    private Set<Transformer2WTypeInput> transformer2WTypes;
    private Set<Transformer3WTypeInput> transformer3WTypes;

    /**
     * Constructor for {@link RawGridTypesCopyBuilder}
     *
     * @param rawGridTypes instance of {@link RawGridTypesCopyBuilder}
     */
    protected RawGridTypesCopyBuilder(RawGridTypes rawGridTypes) {
      this.lineTypes = rawGridTypes.getLineTypes();
      this.cableTypes = rawGridTypes.getCableTypes();
      this.transformer2WTypes = rawGridTypes.getTransformer2WTypes();
      this.transformer3WTypes = rawGridTypes.getTransformer3WTypes();
    }

    /**
     * Method to alter {@link LineTypeInput}
     *
     * @param lineTypes set of altered line types
     * @return this instance of {@link RawGridTypesCopyBuilder}
     */
    public RawGridTypesCopyBuilder lineTypes(Set<LineTypeInput> lineTypes) {
      this.lineTypes = lineTypes;
      return thisInstance();
    }

    /**
     * Method to alter {@link CableTypeInput}
     *
     * @param cableTypes set of altered types
     * @return this instance of {@link RawGridTypesCopyBuilder}
     */
    public RawGridTypesCopyBuilder cableTypes(Set<CableTypeInput> cableTypes) {
      this.cableTypes = cableTypes;
      return thisInstance();
    }

    /**
     * Method to alter {@link Transformer2WTypeInput}
     *
     * @param transformer2WTypes set of altered two winding transformer types
     * @return this instance of {@link RawGridTypesCopyBuilder}
     */
    public RawGridTypesCopyBuilder transformers2WTypes(
        Set<Transformer2WTypeInput> transformer2WTypes) {
      this.transformer2WTypes = transformer2WTypes;
      return thisInstance();
    }

    /**
     * Method to alter {@link Transformer3WTypeInput}
     *
     * @param transformer3WTypes set of altered three winding transformer types
     * @return this instance of {@link RawGridTypesCopyBuilder}
     */
    public RawGridTypesCopyBuilder transformer3WTypes(
        Set<Transformer3WTypeInput> transformer3WTypes) {
      this.transformer3WTypes = transformer3WTypes;
      return thisInstance();
    }

    @Override
    protected RawGridTypesCopyBuilder thisInstance() {
      return this;
    }

    @Override
    public RawGridTypes build() {
      return new RawGridTypes(lineTypes, cableTypes, transformer2WTypes, transformer3WTypes);
    }
  }
}
