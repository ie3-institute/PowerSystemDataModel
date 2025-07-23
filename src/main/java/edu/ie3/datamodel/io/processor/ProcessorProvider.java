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
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.utils.Try;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Wrapper providing the class specific processor to convert an instance of a {@link Entity} into a
 * mapping from attribute to value which can be used to write data e.g. into .csv files. This
 * wrapper can always be used if it's not clear which specific instance of a subclass of {@link
 * Entity} is received in the implementation. It can either be used for specific entity processors
 * only or as a general provider for all known entity processors.
 *
 * @version 0.1
 * @since 20.03.20
 */
public class ProcessorProvider {

  /** unmodifiable map of all processors that has been provided on construction */
  private final Map<Class<? extends Entity>, EntityProcessor<? extends Entity>> entityProcessors;

  private final Map<
          TimeSeriesProcessorKey,
          TimeSeriesProcessor<
              TimeSeries<TimeSeriesEntry<Value>, Value, Value>,
              TimeSeriesEntry<Value>,
              Value,
              Value>>
      timeSeriesProcessors;

  /** Get an instance of this class with all existing entity processors */
  public ProcessorProvider() throws EntityProcessorException {
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
      Collection<EntityProcessor<? extends Entity>> entityProcessors,
      Map<
              TimeSeriesProcessorKey,
              TimeSeriesProcessor<
                  TimeSeries<TimeSeriesEntry<Value>, Value, Value>,
                  TimeSeriesEntry<Value>,
                  Value,
                  Value>>
          timeSeriesProcessors) {
    this.entityProcessors = init(entityProcessors);
    this.timeSeriesProcessors = timeSeriesProcessors;
  }

  public <T extends Entity>
      Try<LinkedHashMap<String, String>, ProcessorProviderException> handleEntity(T entity) {
    return Try.of(() -> getEntityProcessor(entity.getClass()), ProcessorProviderException.class)
        .flatMap(ProcessorProvider::castProcessor)
        .flatMap(
            processor ->
                Try.of(() -> processor.handleEntity(entity), EntityProcessorException.class)
                    .transformF(ProcessorProviderException::new));
  }

  public <T extends Entity> Set<LinkedHashMap<String, String>> handleEntities(List<T> entities)
      throws ProcessorProviderException {
    Set<T> setOfEntities = new HashSet<>(entities);
    Set<LinkedHashMap<String, String>> setOfMaps = new HashSet<>();
    for (T entity : setOfEntities) {
      LinkedHashMap<String, String> entryResult = handleEntity(entity).getOrThrow();

      /* Prepare the actual result and add them to the set of all results */
      setOfMaps.add(new LinkedHashMap<>(entryResult));
    }
    return setOfMaps;
  }

  /**
   * Get the correct entity processor
   *
   * @param clazz Class to process
   * @return The correct entity processor
   * @throws ProcessorProviderException If the processor cannot be found
   */
  private EntityProcessor<? extends Entity> getEntityProcessor(Class<? extends Entity> clazz)
      throws ProcessorProviderException {
    EntityProcessor<? extends Entity> processor = entityProcessors.get(clazz);
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
   * @param <R> Type of the value, the time series will return
   * @return A set of mappings from field name to value
   */
  public <
          T extends TimeSeries<E, V, R>,
          E extends TimeSeriesEntry<V>,
          V extends Value,
          R extends Value>
      Set<LinkedHashMap<String, String>> handleTimeSeries(T timeSeries)
          throws ProcessorProviderException {
    TimeSeriesProcessorKey key = new TimeSeriesProcessorKey(timeSeries);
    return Try.of(
            () -> this.<T, E, V, R>getTimeSeriesProcessor(key), ProcessorProviderException.class)
        .flatMap(
            processor ->
                Try.of(() -> processor.handleTimeSeries(timeSeries), EntityProcessorException.class)
                    .transformF(ProcessorProviderException::new))
        .getOrThrow();
  }

  /**
   * Get the correct processor for this time series combination
   *
   * @param processorKey Combination of time series class, entry class and value class
   * @param <T> Type of the time series
   * @param <E> Type of the entry of the time series
   * @param <V> Type of the entry's value
   * @param <R> Type of the value, the time series will return
   * @return The correct processor
   * @throws ProcessorProviderException If no fitting processor can be found
   */
  @SuppressWarnings("unchecked cast")
  private <
          T extends TimeSeries<E, V, R>,
          E extends TimeSeriesEntry<V>,
          V extends Value,
          R extends Value>
      TimeSeriesProcessor<T, E, V, R> getTimeSeriesProcessor(TimeSeriesProcessorKey processorKey)
          throws ProcessorProviderException {
    TimeSeriesProcessor<T, E, V, R> processor =
        (TimeSeriesProcessor<T, E, V, R>) timeSeriesProcessors.get(processorKey);
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
  public List<Class<? extends Entity>> getRegisteredClasses() {
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
  public String[] getHeaderElements(Class<? extends Entity> clazz)
      throws ProcessorProviderException {
    try {
      EntityProcessor<? extends Entity> processor = getEntityProcessor(clazz);
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
      TimeSeriesProcessor<
              TimeSeries<TimeSeriesEntry<Value>, Value, Value>,
              TimeSeriesEntry<Value>,
              Value,
              Value>
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
  private Map<Class<? extends Entity>, EntityProcessor<? extends Entity>> init(
      Collection<EntityProcessor<? extends Entity>> processors) {

    Map<Class<? extends Entity>, EntityProcessor<? extends Entity>> processorMap = new HashMap<>();

    for (EntityProcessor<? extends Entity> processor : processors) {
      processorMap.put(processor.getRegisteredClass(), processor);
    }

    return Collections.unmodifiableMap(processorMap);
  }

  /**
   * Build a collection of all existing processors
   *
   * @return a collection of all existing processors
   */
  public static Collection<EntityProcessor<? extends Entity>> allEntityProcessors()
      throws EntityProcessorException {
    Collection<EntityProcessor<? extends Entity>> resultingProcessors = new ArrayList<>();
    resultingProcessors.addAll(allInputEntityProcessors());
    resultingProcessors.addAll(allResultEntityProcessors());
    return resultingProcessors;
  }

  /**
   * Build a collection of all input processors
   *
   * @return a collection of all input processors
   */
  public static Collection<EntityProcessor<? extends Entity>> allInputEntityProcessors()
      throws EntityProcessorException {
    Collection<EntityProcessor<? extends Entity>> resultingProcessors = new ArrayList<>();
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
  public static Collection<EntityProcessor<? extends Entity>> allResultEntityProcessors()
      throws EntityProcessorException {
    Collection<EntityProcessor<? extends Entity>> resultingProcessors = new ArrayList<>();
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
  @SuppressWarnings("unchecked")
  public static Map<
          TimeSeriesProcessorKey,
          TimeSeriesProcessor<
              TimeSeries<TimeSeriesEntry<Value>, Value, Value>,
              TimeSeriesEntry<Value>,
              Value,
              Value>>
      allTimeSeriesProcessors() throws EntityProcessorException {
    return Try.scanStream(
            TimeSeriesProcessor.eligibleKeys.stream()
                .map(
                    key ->
                        Try.of(
                            () ->
                                new TimeSeriesProcessor<>(
                                    (Class<TimeSeries<TimeSeriesEntry<Value>, Value, Value>>)
                                        key.getTimeSeriesClass(),
                                    (Class<TimeSeriesEntry<Value>>) key.getEntryClass(),
                                    (Class<Value>) key.getValueClass()),
                            EntityProcessorException.class)),
            "time series processors",
            EntityProcessorException::new)
        .getOrThrow()
        .collect(Collectors.toMap(TimeSeriesProcessor::getRegisteredKey, Function.identity()));
  }

  @SuppressWarnings("unchecked cast")
  private static <T extends Entity>
      Try<EntityProcessor<T>, ProcessorProviderException> castProcessor(
          EntityProcessor<? extends Entity> processor) {
    return Try.of(() -> (EntityProcessor<T>) processor, ClassCastException.class)
        .transformF(
            e ->
                new ProcessorProviderException(
                    "Cannot cast processor with registered class '"
                        + processor.getRegisteredClass().getSimpleName()
                        + "'. This indicates a fatal problem with the processor mapping!"));
  }
}
