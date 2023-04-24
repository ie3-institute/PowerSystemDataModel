/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.exceptions.ProcessorProviderException;
import edu.ie3.datamodel.io.processor.input.InputEntityProcessor;
import edu.ie3.datamodel.io.processor.result.ResultEntityProcessor;
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessor;
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessorKey;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper providing the class specific processor to convert an instance of a {@link UniqueEntity}
 * into a mapping from attribute to value which can be used to write data e.g. into .csv files. This
 * wrapper can always be used if it's not clear which specific instance of a subclass of {@link
 * UniqueEntity} is received in the implementation. It can either be used for specific entity
 * processors only or as a general provider for all known entity processors.
 *
 * @version 0.1
 * @since 20.03.20
 */
public class ProcessorProvider {

  private static final Logger log = LoggerFactory.getLogger(ProcessorProvider.class);

  /** unmodifiable map of all processors that has been provided on construction */
  private final Map<Class<? extends UniqueEntity>, EntityProcessor<? extends UniqueEntity>>
      entityProcessors;

  private final Map<
          TimeSeriesProcessorKey,
          TimeSeriesProcessor<
              TimeSeries<TimeSeriesEntry<Value>, Value>, TimeSeriesEntry<Value>, Value>>
      timeSeriesProcessors;

  /** Get an instance of this class with all existing entity processors */
  public ProcessorProvider() {
    this.entityProcessors = init(allEntityProcessors());
    this.timeSeriesProcessors = allTimeSeriesProcessors();
  }

  /**
   * Get an instance of this class based on the provided collection of processors
   *
   * @param entityProcessors the processors for entities that should be known by this provider
   * @param timeSeriesProcessors the processors for time series that should be known by this
   *     provider
   */
  public ProcessorProvider(
      Collection<EntityProcessor<? extends UniqueEntity>> entityProcessors,
      Map<
              TimeSeriesProcessorKey,
              TimeSeriesProcessor<
                  TimeSeries<TimeSeriesEntry<Value>, Value>, TimeSeriesEntry<Value>, Value>>
          timeSeriesProcessors) {
    this.entityProcessors = init(entityProcessors);
    this.timeSeriesProcessors = timeSeriesProcessors;
  }

  public <T extends UniqueEntity> LinkedHashMap<String, String> handleEntity(T entity)
      throws ProcessorProviderException {
    try {
      EntityProcessor<? extends UniqueEntity> processor = getEntityProcessor(entity.getClass());
      return castProcessor(processor).handleEntity(entity);
    } catch (ProcessorProviderException e) {
      log.error("Exception occurred during entity handling.", e);
      throw e;
    }
  }

  /**
   * Get the correct entity processor
   *
   * @param clazz Class to process
   * @return The correct entity processor
   * @throws ProcessorProviderException If the processor cannot be found
   */
  private EntityProcessor<? extends UniqueEntity> getEntityProcessor(
      Class<? extends UniqueEntity> clazz) throws ProcessorProviderException {
    EntityProcessor<? extends UniqueEntity> processor = entityProcessors.get(clazz);
    if (processor == null) {
      throw new ProcessorProviderException(
          "Cannot find a suitable processor for provided class with name '"
              + clazz.getSimpleName()
              + "'. This provider's processors can process: "
              + entityProcessors.keySet().stream()
                  .map(Class::getSimpleName)
                  .collect(Collectors.joining(",")));
    }
    return processor;
  }

  /**
   * Searches for the right processor and returns its result
   *
   * @param timeSeries Time series to process
   * @param <T> Type of the time series
   * @param <E> Type of the time series entries
   * @param <V> Type of the value inside the time series entries
   * @return A set of mappings from field name to value
   */
  public <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
      Set<LinkedHashMap<String, String>> handleTimeSeries(T timeSeries)
          throws ProcessorProviderException {
    TimeSeriesProcessorKey key = new TimeSeriesProcessorKey(timeSeries);
    try {
      TimeSeriesProcessor<T, E, V> processor = getTimeSeriesProcessor(key);
      return processor.handleTimeSeries(timeSeries);
    } catch (ProcessorProviderException e) {
      log.error("Cannot handle the time series '{}'.", timeSeries, e);
      throw e;
    } catch (EntityProcessorException e) {
      log.error("Error during processing of time series.", e);
      throw e;
    }
  }

  /**
   * Get the correct processor for this time series combination
   *
   * @param processorKey Combination of time series class, entry class and value class
   * @param <T> Type of the time series
   * @param <E> Type of the entry of the time series
   * @param <V> Type of the entry's value
   * @return The correct processor
   * @throws ProcessorProviderException If no fitting processor can be found
   */
  @SuppressWarnings("unchecked cast")
  private <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
      TimeSeriesProcessor<T, E, V> getTimeSeriesProcessor(TimeSeriesProcessorKey processorKey)
          throws ProcessorProviderException {
    TimeSeriesProcessor<T, E, V> processor =
        (TimeSeriesProcessor<T, E, V>) timeSeriesProcessors.get(processorKey);
    if (processor == null)
      throw new ProcessorProviderException(
          "Cannot find processor for time series combination '"
              + processorKey
              + "'. Either your provider is not properly initialized or there is no implementation to process this entity class!)");
    return processor;
  }

  /**
   * Returns all classes that are registered within entity processors known by this provider
   *
   * @return all classes this provider hols a processor for
   */
  public List<Class<? extends UniqueEntity>> getRegisteredClasses() {
    return entityProcessors.values().stream()
        .map(EntityProcessor::getRegisteredClass)
        .collect(Collectors.toList());
  }

  public Set<TimeSeriesProcessorKey> getRegisteredTimeSeriesCombinations() {
    return timeSeriesProcessors.keySet();
  }

  /**
   * Returns the header of a given entity class or throws an exception if no processor for the given
   * class is known by this provider.
   *
   * @param clazz the class the header elements are requested for
   * @return the header elements of the requested class
   * @throws ProcessorProviderException If no matching processor can be found
   */
  public String[] getHeaderElements(Class<? extends UniqueEntity> clazz)
      throws ProcessorProviderException {
    try {
      EntityProcessor<? extends UniqueEntity> processor = getEntityProcessor(clazz);
      return processor.getHeaderElements();
    } catch (ProcessorProviderException e) {
      throw new ProcessorProviderException(
          "Error during determination of header elements for entity class '"
              + clazz.getSimpleName()
              + "'.",
          e);
    }
  }

  /**
   * Returns the header of a given time series combination or throws an exception if no processor
   * for the given combination is known by this provider.
   *
   * @param processorKey Time series combination
   * @return the header elements of the requested class
   * @throws ProcessorProviderException If no matching processor can be found
   */
  public String[] getHeaderElements(TimeSeriesProcessorKey processorKey)
      throws ProcessorProviderException {
    try {
      TimeSeriesProcessor<TimeSeries<TimeSeriesEntry<Value>, Value>, TimeSeriesEntry<Value>, Value>
          processor = getTimeSeriesProcessor(processorKey);
      return processor.getHeaderElements();
    } catch (ProcessorProviderException e) {
      throw new ProcessorProviderException(
          "Error during determination of header elements for time series combination '"
              + processorKey
              + "'.",
          e);
    }
  }

  /**
   * Returns an unmodifiable mapping between entity processors and classes they are able to handle
   * for fast access
   *
   * @param processors the processors that should be known by this provider
   * @return a mapping of all classes and their corresponding processor
   */
  private Map<Class<? extends UniqueEntity>, EntityProcessor<? extends UniqueEntity>> init(
      Collection<EntityProcessor<? extends UniqueEntity>> processors) {

    Map<Class<? extends UniqueEntity>, EntityProcessor<? extends UniqueEntity>> processorMap =
        new HashMap<>();

    for (EntityProcessor<? extends UniqueEntity> processor : processors) {
      processorMap.put(processor.getRegisteredClass(), processor);
    }

    return Collections.unmodifiableMap(processorMap);
  }

  /**
   * Build a collection of all existing processors
   *
   * @return a collection of all existing processors
   */
  public static Collection<EntityProcessor<? extends UniqueEntity>> allEntityProcessors() {
    Collection<EntityProcessor<? extends UniqueEntity>> resultingProcessors = new ArrayList<>();
    resultingProcessors.addAll(allInputEntityProcessors());
    resultingProcessors.addAll(allResultEntityProcessors());
    return resultingProcessors;
  }

  /**
   * Build a collection of all input processors
   *
   * @return a collection of all input processors
   */
  public static Collection<EntityProcessor<? extends UniqueEntity>> allInputEntityProcessors() {
    Collection<EntityProcessor<? extends UniqueEntity>> resultingProcessors = new ArrayList<>();
    for (Class<? extends InputEntity> cls : InputEntityProcessor.eligibleEntityClasses) {
      resultingProcessors.add(new InputEntityProcessor(cls));
    }
    return resultingProcessors;
  }

  /**
   * Build a collection of all result processors
   *
   * @return a collection of all result processors
   */
  public static Collection<EntityProcessor<? extends UniqueEntity>> allResultEntityProcessors() {
    Collection<EntityProcessor<? extends UniqueEntity>> resultingProcessors = new ArrayList<>();
    for (Class<? extends ResultEntity> cls : ResultEntityProcessor.eligibleEntityClasses) {
      resultingProcessors.add(new ResultEntityProcessor(cls));
    }
    return resultingProcessors;
  }

  /**
   * Create processors for all known eligible combinations and map them
   *
   * @return A mapping from eligible combinations to processors
   */
  public static Map<
          TimeSeriesProcessorKey,
          TimeSeriesProcessor<
              TimeSeries<TimeSeriesEntry<Value>, Value>, TimeSeriesEntry<Value>, Value>>
      allTimeSeriesProcessors() {
    return TimeSeriesProcessor.eligibleKeys.stream()
        .collect(
            Collectors.toMap(
                key -> key,
                key ->
                    new TimeSeriesProcessor<>(
                        (Class<TimeSeries<TimeSeriesEntry<Value>, Value>>) key.getTimeSeriesClass(),
                        (Class<TimeSeriesEntry<Value>>) key.getEntryClass(),
                        (Class<Value>) key.getValueClass())));
  }

  @SuppressWarnings("unchecked cast")
  private static <T extends UniqueEntity> EntityProcessor<T> castProcessor(
      EntityProcessor<? extends UniqueEntity> processor) throws ProcessorProviderException {
    try {
      return (EntityProcessor<T>) processor;
    } catch (ClassCastException ex) {
      throw new ProcessorProviderException(
          "Cannot cast processor with registered class '"
              + processor.getRegisteredClass().getSimpleName()
              + "'. This indicates a fatal problem with the processor mapping!");
    }
  }
}
