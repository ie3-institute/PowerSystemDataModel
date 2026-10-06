/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory;

import static edu.ie3.datamodel.utils.CollectionUtils.expandSet;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.naming.FieldNamingStrategy;
import edu.ie3.datamodel.io.naming.ModelFields;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.Failure;
import edu.ie3.datamodel.utils.Try.Success;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract factory class, that is able to transfer specific "flat" information in to actual model
 * class instances.
 *
 * @param <C> Type of the intended target class.
 * @param <R> Type of the intended return type (might differ slightly from target class (cf. {@link
 *     edu.ie3.datamodel.io.factory.timeseries.TimeBasedValueFactory})).
 */
public abstract class Factory<C, D, R> extends FieldNamingStrategy implements FactoryHelperMethods {
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
   * @param data of the model
   * @return An entity wrapped in a {@link Success} if successful, or an exception wrapped in a
   *     {@link Failure}
   */
  public Try<R, FactoryException> get(D data) {

    try {
      // build the model
      return Success.of(buildModel(data));
    } catch (FactoryException | IllegalArgumentException e) {
      return Failure.of(
          new FactoryException("An error occurred in " + this.getClass().getSimpleName() + ".", e));
    }
  }

  /**
   * Builds entity with data from given EntityData object after doing all kinds of checks on the
   * data
   *
   * @param data of the model
   * @return An entity wrapped in a {@link Success} if successful, or an exception wrapped in a
   *     {@link Failure}
   */
  public Try<R, FactoryException> get(Try<D, ?> data) {
    return data.transformF(e -> new FactoryException(e.getMessage(), e)).flatMap(this::get);
  }

  /**
   * Builds model with data. Throws {@link FactoryException} if something goes wrong.
   *
   * @param data of the model
   * @return model created from data
   * @throws FactoryException if the model cannot be build
   */
  protected abstract R buildModel(D data);

  /**
   * Checks, if the specific given class can be handled by this factory.
   *
   * @param desiredClass Class that should be built
   */
  protected void isSupportedClass(Class<?> desiredClass) {
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
  protected List<Set<String>> getFields(Class<? extends C> clazz) {
    if (!supportedClasses.contains(clazz)) {
      throw new FactoryException("The given factory cannot handle target class '" + clazz + "'.");
    }

    List<Set<String>> fieldSets = new ArrayList<>(ModelFields.getMandatoryFields(clazz));

    for (String optional : ModelFields.getOptionalFields(clazz)) {
      List<Set<String>> tmp = new ArrayList<>(fieldSets);

      for (Set<String> set : fieldSets) {
        tmp.add(expandSet(set, optional));
      }

      fieldSets = tmp;
    }

    return fieldSets;
  }
}
