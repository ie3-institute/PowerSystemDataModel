/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory;

import edu.ie3.datamodel.models.UniqueEntity;
import java.util.*;

@Deprecated

/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 04.04.20
 */
public class FactoryProvider {

  /** unmodifiable map of all factories that has been provided on construction */
  private final Map<
          Class<? extends UniqueEntity>,
          EntityFactory<? extends UniqueEntity, ? extends EntityData>>
      factories;

  // todo way to pass in fieldsToAttributes + entityClass ->

  /** Get an instance of this class with all existing entity factories */
  public FactoryProvider() {
    this.factories = init(allFactories());
  }

  /**
   * todo
   *
   * @param factories
   */
  public FactoryProvider(
      Collection<EntityFactory<? extends UniqueEntity, ? extends EntityData>> factories) {
    this.factories = init(factories);
  }

  /**
   * // todo
   *
   * @param factories
   * @return
   */
  private Map<
          Class<? extends UniqueEntity>,
          EntityFactory<? extends UniqueEntity, ? extends EntityData>>
      init(Collection<EntityFactory<? extends UniqueEntity, ? extends EntityData>> factories) {

    Map<Class<? extends UniqueEntity>, EntityFactory<? extends UniqueEntity, ? extends EntityData>>
        factoriesMap = new HashMap<>();

    for (EntityFactory<? extends UniqueEntity, ? extends EntityData> factory : factories) {
      for (Class<? extends UniqueEntity> cls : factory.classes()) {
        factoriesMap.put(cls, factory);
      }
    }

    return Collections.unmodifiableMap(factoriesMap);
  }

  /**
   * Build a collection of all existing processors
   *
   * @return a collection of all existing processors
   */
  private Collection<EntityFactory<? extends UniqueEntity, ? extends EntityData>> allFactories() {

    Collection<EntityFactory<? extends UniqueEntity, ? extends EntityData>> resultingFactories =
        new ArrayList<>();

    // todo add missing factories here
    // Input Entity Processor
    //        for (Class<? extends InputEntity> cls : InputEntityProcessor.eligibleEntityClasses) {
    //            resultingFactories.add(new InputEntityProcessor(cls));
    //        }
    //
    //        // Result Entity Processor
    //        for (Class<? extends ResultEntity> cls : ResultEntityProcessor.eligibleEntityClasses)
    // {
    //            resultingFactories.add(new ResultEntityProcessor(cls));
    //        }

    return resultingFactories;
  }
}
