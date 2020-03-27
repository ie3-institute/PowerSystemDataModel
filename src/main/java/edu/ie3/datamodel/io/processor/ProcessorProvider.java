/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor;

import edu.ie3.datamodel.exceptions.ProcessorProviderException;
import edu.ie3.datamodel.io.processor.input.AssetInputProcessor;
import edu.ie3.datamodel.io.processor.input.GraphicInputProcessor;
import edu.ie3.datamodel.io.processor.result.ResultEntityProcessor;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.result.ResultEntity;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Wrapper providing the class specific processor to convert an instance of a {@link UniqueEntity}
 * into a mapping of <attribute,value> which can be used to write data e.g. into .csv files. This
 * wrapper can always be used if it's not clear which specific instance of a subclass of {@link
 * UniqueEntity} is received in the implementation. It can either be used for specific entity
 * processors only or as a general provider for all known entity processors.
 *
 * @version 0.1
 * @since 20.03.20
 */
public class ProcessorProvider {

  private static final Logger log = LogManager.getLogger(ProcessorProvider.class);

  /** unmodifiable map of all processors that has been provided on construction */
  private final Map<Class<? extends UniqueEntity>, EntityProcessor<? extends UniqueEntity>>
      processors;

  /** Get an instance of this class with all existing entity processors */
  public ProcessorProvider() {
    this.processors = init(allProcessors());
  }

  /**
   * Get an instance of this class based on the provided collection of processors
   *
   * @param processors the processors that should be known by this provider
   */
  public ProcessorProvider(Collection<EntityProcessor<? extends UniqueEntity>> processors) {
    this.processors = init(processors);
  }

  public <T extends UniqueEntity> Optional<LinkedHashMap<String, String>> processEntity(T entity) {
    EntityProcessor<? extends UniqueEntity> processor = processors.get(entity.getClass());
    if (processor == null) {
      log.warn(
          "Cannot find processor for entity class '{}'."
              + "Either your provider is not properly initialized or "
              + "there is no implementation to process this entity class!)",
          entity.getClass().getSimpleName());

      return Optional.empty();
    }

    try {
      return castProcessor(processor).handleEntity(entity);
    } catch (ProcessorProviderException e) {
      log.error("Exception occurred during processor casting.", e);
      return Optional.empty();
    }
  }

  /**
   * Returns all classes that are registered within entity processors known by this provider
   *
   * @return all classes this provider hols a processor for
   */
  public List<Class<? extends UniqueEntity>> getRegisteredClasses() {
    return processors.values().stream()
        .map(EntityProcessor::getRegisteredClass)
        .collect(Collectors.toList());
  }

  /**
   * Returns the header of a given class or throws an exception if no processor for the given class
   * is known by this provider.
   *
   * @param clazz the class the header elements are requested for
   * @return the header elements of the requested class
   */
  public Optional<String[]> getHeaderElements(Class<? extends UniqueEntity> clazz) {
    EntityProcessor<? extends UniqueEntity> processor = processors.get(clazz);
    if (processor == null) {
      log.error(
          "Cannot find a suitable processor for provided class with name '{}'. "
              + "This provider's processors can process: {}",
          clazz.getSimpleName(),
          processors.keySet().stream().map(Class::getSimpleName).collect(Collectors.joining(",")));
      return Optional.empty();
    }

    return Optional.ofNullable(processor.getHeaderElements());
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
  private Collection<EntityProcessor<? extends UniqueEntity>> allProcessors() {

    Collection<EntityProcessor<? extends UniqueEntity>> resultingProcessors = new ArrayList<>();

    // todo add missing processors here
    // AssetInput
    for (Class<? extends AssetInput> cls : AssetInputProcessor.eligibleEntityClasses) {
      resultingProcessors.add(new AssetInputProcessor(cls));
    }

    // GraphicInput
    for (Class<? extends GraphicInput> cls : GraphicInputProcessor.eligibleEntityClasses) {
      resultingProcessors.add(new GraphicInputProcessor(cls));
    }

    // SystemParticipantResults
    for (Class<? extends ResultEntity> cls : ResultEntityProcessor.eligibleEntityClasses) {
      resultingProcessors.add(new ResultEntityProcessor(cls));
    }

    return resultingProcessors;
  }

  @SuppressWarnings("unchecked cast")
  private <T extends UniqueEntity> EntityProcessor<T> castProcessor(
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
