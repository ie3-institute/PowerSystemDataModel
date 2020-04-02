/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.sink;

import edu.ie3.datamodel.io.connectors.DataConnector;
import edu.ie3.datamodel.io.processor.EntityProcessor;
import edu.ie3.datamodel.models.UniqueEntity;
import java.util.Collection;

/**
 * Describes a class that manages data persistence. A sample implementation that can be used as a
 * blueprint for all method implementation incl. entity handling with processors can be found in
 * {@link CsvFileSink}
 */
public interface DataSink {

  /** @return the connector of this sink */
  DataConnector getDataConnector();

  /**
   * Should implement the entry point of a data sink to persist an entity. By default this method
   * should take care about the extraction process of nested entities (if any) and use {@link
   * edu.ie3.datamodel.io.extractor.Extractor} accordingly. For an faster method e.g. that neglects
   * the nested objects persistence and only persists the uuid of the nested objects (if any),
   * instead of the object itself use {@link DataSink#persistIgnoreNested}
   *
   * @param entity the entity that should be persisted
   * @param <C> bounded to be all unique entities. Handling of specific entities is normally then
   *     executed by a specific {@link EntityProcessor}
   */
  <C extends UniqueEntity> void persist(C entity);

  /**
   * Should implement the entry point of a data sink to persist multiple entities in a collection.
   * By default this method should take care about the extraction process of nested entities (if
   * any) and use {@link edu.ie3.datamodel.io.extractor.Extractor} accordingly. For an faster method
   * e.g. that neglects the nested objects persistence and only persists the uuid of the nested
   * objects (if any), instead of the object itself use {@link DataSink#persistAllIgnoreNested}
   *
   * @param entities a collection of entities that should be persisted
   * @param <C> bounded to be all unique entities. Handling of specific entities is normally then
   *     executed by a specific {@link EntityProcessor}
   */
  <C extends UniqueEntity> void persistAll(Collection<C> entities);

  /**
   * Should implement the entry point of a data sink to persist an entity. In contrast to {@link
   * DataSink#persist}, this method should <b>not</b> take care about the extraction process of
   * nested entities (if any) but only persist the uuid of the nested entity. This <b>might</b>
   * speed up things a little bit because of missing if-/else-clauses but can also lead to missing
   * persisted data that should be persisted, but is not e.g. nested types that are not available
   * anymore afterwards. It might be useful especially for all entities without nested entities. For
   * all doubts about if the provided entity contains needed nested data or not {@link
   * DataSink#persist} is the recommended method to be used.
   *
   * @param entity the entity that should be persisted
   * @param <C> bounded to be all unique entities. Handling of specific entities is normally then
   *     executed by a specific {@link EntityProcessor}
   */
  <C extends UniqueEntity> void persistIgnoreNested(C entity);

  /**
   * Should implement the entry point of a data sink to persist multiple entities in a collection.
   * In contrast to {@link DataSink#persistAll}, this method should <b>not</b> take care about the
   * extraction process of nested entities (if any) but only persist the uuid of the nested entity.
   * This <b>might</b> speed up things a little bit because of missing if-/else-clauses but but can
   * also lead to missing persisted data that should be persisted, but is not e.g. nested types that
   * are not available anymore afterwards. It might be useful especially for all entities without
   * nested entities. For all doubts about if the provided entity contains needed nested data or not
   * {@link DataSink#persistAll} is the recommended method to be used.
   *
   * @param entities the entities that should be persisted
   * @param <C> bounded to be all unique entities. Handling of specific entities is normally then
   *     executed by a specific {@link EntityProcessor}
   */
  <C extends UniqueEntity> void persistAllIgnoreNested(Collection<C> entities);
}
