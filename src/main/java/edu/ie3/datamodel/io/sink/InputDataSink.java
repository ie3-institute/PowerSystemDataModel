/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.sink;

import edu.ie3.datamodel.io.processor.input.InputEntityProcessor;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.input.container.JointGridContainer;
import java.util.Collection;

public interface InputDataSink extends DataSink {

  /**
   * Should implement the entry point of a data sink to persist an input entity. In contrast to
   * {@link DataSink#persist} and {@link InputDataSink#persistIncludeNested}, this method should
   * <b>not</b> take care about the extraction process of nested entities (if any) but only persist
   * the uuid of the nested entity. This <b>might</b> speed up things a little bit because of
   * missing if-/else-clauses but can also lead to missing persisted data that should be persisted,
   * but is not e.g. nested types that are not available anymore afterwards. It might be useful
   * especially for all entities without nested entities. For all doubts about if the provided
   * entity contains needed nested data or not {@link DataSink#persist(UniqueEntity)} is the
   * recommended method to be used.
   *
   * @param entity the entity that should be persisted
   * @param <C> bounded to be all input entities. Handling of the entities is normally then executed
   *     by a {@link InputEntityProcessor}
   */
  <C extends InputEntity> void persistIgnoreNested(C entity);

  /**
   * Should implement the entry point of a data sink to persist multiple input entities in a
   * collection. In contrast to {@link DataSink#persistAll(Collection)} and {@link
   * InputDataSink#persistAllIncludeNested}, this method should <b>not</b> take care about the
   * extraction process of nested entities (if any) but only persist the uuid of the nested entity.
   * This <b>might</b> speed up things a little bit because of missing if-/else-clauses but but can
   * also lead to missing persisted data that should be persisted, but is not e.g. nested types that
   * are not available anymore afterwards. It might be useful especially for all entities without
   * nested entities. For all doubts about if the provided entity contains needed nested data or not
   * {@link DataSink#persistAll(Collection)} is the recommended method to be used.
   *
   * @param entities the entities that should be persisted
   * @param <C> bounded to be all unique entities. Handling of the entities is normally then
   *     executed by a {@link InputEntityProcessor}
   */
  <C extends InputEntity> void persistAllIgnoreNested(Collection<C> entities);

  /**
   * Should offer a clear alternative to {@link InputDataSink#persistIgnoreNested} if the nested
   * entities of an input entity are supposed to be persisted as well. However this might take
   * longer as additional entities have to be extracted and persisted.
   *
   * @param entity the entity that should be persisted including its nested entities
   * @param <C> bounded to be all input entities. Handling of specific entities is normally then
   *     executed by a specific {@link InputEntityProcessor}
   */
  <C extends InputEntity> void persistIncludeNested(C entity);

  /**
   * Should offer a clear alternative to {@link InputDataSink#persistAllIgnoreNested} if the nested
   * entities of the input entities are supposed to be persisted as well. However this might take
   * longer as additional entities have to be extracted and persisted.
   *
   * @param entities the entities that should be persisted including its nested entities
   * @param <C> bounded to be all unique entities. Handling of the entities is normally then
   *     executed by a {@link InputEntityProcessor}
   */
  <C extends InputEntity> void persistAllIncludeNested(Collection<C> entities);

  /**
   * Should implement the entry point of a data sink to persist a whole {@link JointGridContainer}
   *
   * @param jointGridContainer the {@link JointGridContainer} that should be persisted
   */
  void persistJointGrid(JointGridContainer jointGridContainer);
}
