/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.sink;

import edu.ie3.datamodel.exceptions.ProcessorProviderException;
import edu.ie3.datamodel.io.connectors.DataConnector;
import edu.ie3.datamodel.io.processor.EntityProcessor;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import java.util.Collection;

/**
 * Describes a class that manages data persistence. A sample implementation that can be used as a
 * blueprint for all method implementation incl. entity handling with processors can be found in
 * {@link CsvFileSink}
 */
public interface DataSink {

  /**
   * Shutdown this sink and do all cleanup operations (e.g. closing of the {@link DataConnector})
   * here
   */
  void shutdown();

  /**
   * Should implement the entry point of a data sink to persist an entity. By default this method
   * should take care about the extraction process of nested entities (if any) of input entities and
   * use {@link edu.ie3.datamodel.io.extractor.Extractor} accordingly. For an faster method e.g.
   * that neglects the nested objects persistence and only persists the uuid of the nested objects
   * (if any), instead of the object itself use {@link InputDataSink#persistIgnoreNested}
   *
   * @param <C> bounded to be all unique entities. Handling of specific entities is normally then
   *     executed by a specific {@link EntityProcessor}
   * @param entity the entity that should be persisted
   * @throws ProcessorProviderException the processor provider exception
   */
  <C extends Entity> void persist(C entity) throws ProcessorProviderException;

  /**
   * Should implement the entry point of a data sink to persist multiple entities in a collection.
   * By default this method should take care about the extraction process of nested entities (if
   * any) of input entities and use {@link edu.ie3.datamodel.io.extractor.Extractor} accordingly.
   * For a faster method that neglects the nested objects persistence and only persists the uuid of
   * the nested * objects (if any), instead of the object itself use {@link
   * InputDataSink#persistAllIgnoreNested}*
   *
   * @param <C> bounded to be all unique entities. Handling of specific entities is normally then
   *     executed by a specific {@link EntityProcessor}
   * @param entities a collection of entities that should be persisted
   * @throws ProcessorProviderException the processor provider exception
   */
  <C extends Entity> void persistAll(Collection<C> entities) throws ProcessorProviderException;

  /**
   * Should implement the handling of a whole time series. Therefore the single entries have to be
   * extracted and persisted accordingly.
   *
   * @param <E> Type of entry in the time series
   * @param <V> Type of actual value, that is inside the entry
   * @param <R> Type of the value, the time series will return
   * @param timeSeries Time series to persist
   * @throws ProcessorProviderException the processor provider exception
   */
  <E extends TimeSeriesEntry<V>, V extends Value, R extends Value> void persistTimeSeries(
      TimeSeries<E, V, R> timeSeries) throws ProcessorProviderException;
}
