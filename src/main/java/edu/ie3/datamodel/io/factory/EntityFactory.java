/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.util.TimeTools;
import java.time.ZoneId;
import java.util.*;

/**
 * Universal factory class for creating entities with {@link EntityData} data objects.
 *
 * @param <T> Type of entity that this factory can create. Can be a subclass of the entities that
 *     this factory creates.
 * @param <D> Type of data class that is required for entity creation
 * @version 0.1
 * @since 28.01.20
 */
public abstract class EntityFactory<T extends UniqueEntity, D extends EntityData>
    extends Factory<T, D, T> {
  /**
   * Constructor for an EntityFactory for given classes
   *
   * @param allowedClasses exactly the classes that this factory is allowed and able to build
   */
  public EntityFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
    TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss");
  }

  /**
   * Expands a set of attributes with further attributes. This method should always be used when
   * returning attribute sets, i.e. through {@link #getFields(EntityData)}.
   *
   * @param attributeSet set of attributes to expand
   * @param more attribute names to expand given set with
   * @return new set exactly containing given attribute set plus additional attributes
   */
  protected TreeSet<String> expandSet(Set<String> attributeSet, String... more) {
    TreeSet<String> newSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    newSet.addAll(attributeSet);
    newSet.addAll(Arrays.asList(more));
    return newSet;
  }
}
