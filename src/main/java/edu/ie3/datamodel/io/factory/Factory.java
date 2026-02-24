/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.naming.FieldNames;
import edu.ie3.datamodel.io.naming.FieldNaming;
import edu.ie3.datamodel.io.source.DataSource;
import edu.ie3.datamodel.io.source.SourceValidator;
import edu.ie3.datamodel.utils.CollectionUtils;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.Failure;
import edu.ie3.datamodel.utils.Try.Success;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstract factory class, that is able to transfer specific "flat" information in to actual model
 * class instances.
 *
 * @param <C> Type of the intended target class.
 * @param <D> Type of the "flat" information.
 * @param <R> Type of the intended return type (might differ slightly from target class (cf. {@link
 *     edu.ie3.datamodel.io.factory.timeseries.TimeBasedValueFactory})).
 */
public abstract class Factory<C, D extends FactoryData, R>
    implements FieldNames, SourceValidator<C> {
  public static final Logger log = LoggerFactory.getLogger(Factory.class);

  private final List<Class<? extends C>> supportedClasses;

  @SafeVarargs
  protected Factory(Class<? extends C>... supportedClasses) {
    this.supportedClasses = Arrays.asList(supportedClasses);
  }

  public List<Class<? extends C>> getSupportedClasses() {
    return supportedClasses;
  }

  /**
   * Builds entity with data from given EntityData object after doing all kinds of checks on the
   * data
   *
   * @param data EntityData (or subclass) containing the data
   * @return An entity wrapped in a {@link Success} if successful, or an exception wrapped in a
   *     {@link Failure}
   */
  public Try<R, FactoryException> get(D data) {
    isSupportedClass(data.getTargetClass());

    try {
      // build the model
      return Success.of(buildModel(data));
    } catch (FactoryException | IllegalArgumentException e) {
      return Failure.of(
          new FactoryException(
              "An error occurred when creating instance of "
                  + data.getTargetClass().getSimpleName()
                  + ".class.",
              e));
    }
  }

  /**
   * Builds entity with data from given EntityData object after doing all kinds of checks on the
   * data
   *
   * @param data EntityData (or subclass) containing the data wrapped in a {@link Try}
   * @return An entity wrapped in a {@link Success} if successful, or an exception wrapped in a
   *     {@link Failure}
   */
  public Try<R, FactoryException> get(Try<D, ?> data) {
    return data.transformF(e -> new FactoryException(e.getMessage(), e)).flatMap(this::get);
  }

  /**
   * Builds model with data from given {@link FactoryData} object. Throws {@link FactoryException}
   * if something goes wrong.
   *
   * @param data {@link FactoryData} (or subclass) containing the data
   * @return model created from data
   * @throws FactoryException if the model cannot be build
   */
  protected abstract R buildModel(D data);

  /**
   * Checks, if the specific given class can be handled by this factory.
   *
   * @param desiredClass Class that should be built
   */
  private void isSupportedClass(Class<?> desiredClass) {
    if (!supportedClasses.contains(desiredClass))
      throw new FactoryException(
          "Cannot process "
              + desiredClass.getSimpleName()
              + ".class with this factory!\nThis factory can only process the following classes:\n - "
              + supportedClasses.stream()
                  .map(Class::getSimpleName)
                  .collect(Collectors.joining("\n - ")));
  }

  /**
   * Returns list of sets of attribute names that the entity requires to be built. At least one of
   * these sets needs to be delivered for entity creation to be successful.
   *
   * @param clazz class that can be used to specify the fields that are returned
   * @return list of possible attribute sets
   */
  protected List<Set<String>> getFields(Class<?> clazz) {
    if (!supportedClasses.contains(clazz)) {
      throw new FactoryException("The given factory cannot handle target class '" + clazz + "'.");
    }

    List<Set<String>> fieldSets = new ArrayList<>(FieldNaming.getMandatoryFields(clazz));

    for (String optional : FieldNaming.getOptionalFields(clazz)) {
      List<Set<String>> tmp = new ArrayList<>(fieldSets);

      for (Set<String> set : fieldSets) {
        tmp.add(expandSet(set, optional));
      }

      fieldSets = tmp;
    }

    return fieldSets;
  }

  /**
   * Method for validating the actual fields. The actual fields need to fully contain at least one
   * of the sets returned by {@link #getFields(Class)}. If the actual fields don't contain all
   * necessary fields, an {@link FactoryException} with a detail message is thrown. If the actual
   * fields contain more fields than necessary, these fields are ignored.
   *
   * @param actualFields that were found
   * @param entityClass of the build data
   * @return either an exception wrapped by a {@link Failure} or an empty success
   * @deprecated Use {@link DataSource#validate(Set, Class)} instead.
   */
  @Deprecated
  public Try<Void, ValidationException> validate(
      Set<String> actualFields, Class<? extends C> entityClass) {
    return DataSource.validate(
        actualFields,
        entityClass,
        getFields(entityClass),
        Collections.emptySet(),
        Collections.emptySet());
  }

  /**
   * Creates a new set of attribute names from given list of attributes. This method should always
   * be used when returning attribute sets, i.e. through {@link #getFields(Class)}.
   *
   * @param attributes attribute names
   * @return new set exactly containing attribute names
   */
  protected static TreeSet<String> newSet(String... attributes) {
    return CollectionUtils.newSet(attributes);
  }

  /**
   * Expands a set of attributes with further attributes. This method should always be used when
   * returning attribute sets, i.e. through getting the needed fields. The set maintains a
   * lexicographic order, that is case-insensitive.
   *
   * @param attributeSet set of attributes to expand
   * @param more attribute names to expand given set with
   * @return new set exactly containing given attribute set plus additional attributes
   */
  protected static TreeSet<String> expandSet(Set<String> attributeSet, String... more) {
    return CollectionUtils.expandSet(attributeSet, more);
  }
}
