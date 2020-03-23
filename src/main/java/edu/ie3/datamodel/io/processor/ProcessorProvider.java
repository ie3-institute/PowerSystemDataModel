/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor;

import edu.ie3.datamodel.exceptions.ProcessorProviderException;
import edu.ie3.datamodel.io.processor.result.SystemParticipantResultProcessor;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.result.system.SystemParticipantResult;
import java.util.*;
import java.util.stream.Collectors;

/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 20.03.20
 */
public class ProcessorProvider {

  /** unmodifiable map of all processors that has been provided on construction */
  private final Map<Class<? extends UniqueEntity>, EntityProcessor<? extends UniqueEntity>>
      processors;

  public ProcessorProvider() {
    this.processors = init(allProcessors());
  }

  public ProcessorProvider(Collection<EntityProcessor<? extends UniqueEntity>> processors) {
    this.processors = init(processors);
  }

  /**
   * Returns an unmodifiable mapping between entity processors and classes they are able to handle
   * for fast access // todo
   *
   * @param processors
   * @return
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

  private Collection<EntityProcessor<? extends UniqueEntity>> allProcessors() {

    Collection<EntityProcessor<? extends UniqueEntity>> resultingProcessors = new ArrayList<>();

    // todo add missing processors here

    // SystemParticipantResults
    for (Class<? extends SystemParticipantResult> cls :
        SystemParticipantResultProcessor.processorEntities) {
      resultingProcessors.add(new SystemParticipantResultProcessor(cls));
    }

    return resultingProcessors;
  }

  public <T extends UniqueEntity> Optional<LinkedHashMap<String, String>> processEntity(T entity)
      throws ProcessorProviderException {
    EntityProcessor<? extends UniqueEntity> processor = processors.get(entity.getClass());
    if (processor == null) {
      throw new ProcessorProviderException(
          "Cannot find processor for entity class '"
              + entity.getClass().getSimpleName()
              + "'."
              + "Either your provider is not properly initialized or there is no implementation to process this entity class!)");
    }
    return castProcessor(processor).handleEntity(entity);
  }

  //
  //    public <T extends UniqueEntity> EntityProcessor<T> getProcessor(Class<T> entityClass) throws
  //                    ProcessorProviderException {
  //        EntityProcessor<? extends UniqueEntity> processor = processors.get(entityClass);
  //        if(processor == null) {
  //            throw new ProcessorProviderException(
  //                            "Cannot find processor for entity class '" +
  // entityClass.getSimpleName() + "'." +
  //                            "Either your provider is not properly initialized or there is no
  // implementation to process this entity class!)");
  //        }
  //        return castProcessor(processor).handleEntity(entityClass);
  //    }

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

  public List<Class<? extends UniqueEntity>> getRegisteredClasses() {
    return processors.values().stream()
        .map(EntityProcessor::getRegisteredClass)
        .collect(Collectors.toList());
  }

  public String[] getHeaderElements(Class<? extends UniqueEntity> clazz) {
    return processors.get(clazz).getHeaderElements();
  }

  //    public <T extends UniqueEntity> Collection<EntityProcessor<T>> getProcessors() throws
  // ProcessorProviderException {
  //
  //        List<EntityProcessor<T>> resProcessors = new ArrayList<>();
  //
  //        for(EntityProcessor<? extends UniqueEntity> processor : processors.values()) {
  //            resProcessors.add(castProcessor(processor));
  //        }
  //
  //        return resProcessors;
  //
  //    }

}
