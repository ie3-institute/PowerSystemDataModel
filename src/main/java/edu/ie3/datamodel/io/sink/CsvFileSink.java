/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.sink;

import edu.ie3.datamodel.exceptions.ExtractorException;
import edu.ie3.datamodel.exceptions.SinkException;
import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.connectors.DataConnector;
import edu.ie3.datamodel.io.extractor.Extractor;
import edu.ie3.datamodel.io.extractor.Nested;
import edu.ie3.datamodel.io.extractor.Type;
import edu.ie3.datamodel.io.processor.ProcessorProvider;
import edu.ie3.datamodel.models.UniqueEntity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import edu.ie3.datamodel.models.input.InputEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Sink that provides all capabilities to write {@link UniqueEntity}s to .csv-files
 *
 * @version 0.1
 * @since 19.03.20
 */
public class CsvFileSink implements DataSink {

    private static final Logger log = LogManager.getLogger(CsvFileSink.class);

    private final CsvFileConnector  connector;
    private final ProcessorProvider processorProvider;

    private final String csvSep;

    public CsvFileSink(String baseFolderPath) {
        this(baseFolderPath, new FileNamingStrategy(), false, ",");
    }

    /**
     * Create an instance of a csv file sink that can be used to persist Unique entities.
     * This implementation processes in sequential order. To parallelize this process one
     * might consider starting several sinks and use them for specific entities.
     *
     * @param baseFolderPath     the base folder path where the files should be put into
     * @param fileNamingStrategy the file naming strategy that should be used
     * @param initFiles          true if the files should be created during initialization (might create files,
     *                           that only consist of a headline, because no data will be writen into them), false otherwise
     * @param csvSep             the csv file separator that should be use
     */
    public CsvFileSink(String baseFolderPath, FileNamingStrategy fileNamingStrategy, boolean initFiles, String csvSep) {
        this.csvSep = csvSep;
        this.processorProvider = new ProcessorProvider();
        this.connector = new CsvFileConnector(baseFolderPath, fileNamingStrategy);

        if(initFiles)
            initFiles(processorProvider, connector);
    }

    /**
     * Create an instance of a csv file sink that can be used to persist Unique entities.
     * This implementation processes in sequential order. To parallelize this process one
     * might consider starting several sinks and use them for specific entities.
     * Be careful when providing your own {@link ProcessorProvider} because if you're not 100% sure that
     * it knows about all entities you're going to process exceptions might occur. Therefore it is strongly
     * advised to either use a constructor without providing the {@link ProcessorProvider} or
     * provide a general {@link ProcessorProvider} by calling {@link ProcessorProvider()}
     *
     * @param baseFolderPath     the base folder path where the files should be put into
     * @param processorProvider  the processor provided that should be used for entity de-serialization
     * @param fileNamingStrategy the file naming strategy that should be used
     * @param initFiles          true if the files should be created during initialization (might create files,
     *                           that only consist of a headline, because no data will be writen into them), false otherwise
     * @param csvSep             the csv file separator that should be use
     */
    public CsvFileSink(String baseFolderPath,
                       ProcessorProvider processorProvider,
                       FileNamingStrategy fileNamingStrategy,
                       boolean initFiles,
                       String csvSep) {
        this.csvSep = csvSep;
        this.processorProvider = processorProvider;
        this.connector = new CsvFileConnector(baseFolderPath, fileNamingStrategy);

        if(initFiles)
            initFiles(processorProvider, connector);
    }

    @Override
    public DataConnector getDataConnector() {
        return connector;
    }

    @Override
    public <T extends UniqueEntity> void persistAll(Collection<T> entities) {
        for(T entity : entities) {
            persist(entity);
        }
    }

    @Override
    public <C extends UniqueEntity> void persistAllIgnoreNested(C entity) {
        LinkedHashMap<String, String> entityFieldData = processorProvider.processEntity(entity)
                        .orElseThrow(() -> new SinkException(
                                        "Cannot persist entity of type '" + entity.getClass().getSimpleName() +
                                        "'. This sink can only process the following entities: [" +
                                        processorProvider.getRegisteredClasses().stream().map(Class::getSimpleName)
                                                        .collect(Collectors.joining(",")) + "]"));

        String[] headerElements = processorProvider.getHeaderElements(entity.getClass()).orElse(new String[0]);
        BufferedWriter writer = connector.getOrInitWriter(entity.getClass(), headerElements, csvSep);
        write(entityFieldData, headerElements, writer);
    }

    @Override
    public <C extends UniqueEntity> void persistAllIgnoreNested(Collection<C> entities) {
        entities.parallelStream().forEach(this::persistAllIgnoreNested);
    }

    @Override
    public <T extends UniqueEntity> void persist(T entity) {
        if(entity instanceof Nested) {
            try {
                Extractor extractor = new Extractor((Type) entity);
                for(InputEntity ent : extractor.getExtractedEntities()) {
                    persistAllIgnoreNested(ent);
                }

            } catch(ExtractorException e) {
                log.error("An error occurred during extraction of nested entity'" + entity.getClass().getSimpleName() +
                          "': ", e);
            }
        } else {
            persistAllIgnoreNested(entity);
        }

    }

    /**
     * Initialize files, hence create a file for each expected class that will be processed in the
     * future.
     *
     * @param processorProvider the processor provider all files that will be processed is derived
     *                          from
     * @param connector         the connector to the files
     */
    private void initFiles(final ProcessorProvider processorProvider, final CsvFileConnector connector) {

        processorProvider.getRegisteredClasses().forEach(clz -> processorProvider.getHeaderElements(clz)
                        .ifPresent(headerElements -> connector.getOrInitWriter(clz, headerElements, csvSep)));
    }

    /**
     * Actually persisting the provided entity field data
     *
     * @param entityFieldData a mapping of an entity instance fields to their values
     * @param headerElements  the header elements of the entity, normally all attributes of the entity
     *                        class
     * @param writer          the corresponding writer for that should be used
     */
    private void write(LinkedHashMap<String, String> entityFieldData, String[] headerElements, BufferedWriter writer) {

        try {
            for(int i = 0; i < headerElements.length; i++) {
                String attribute = headerElements[i];
                writer.append(entityFieldData.get(attribute));
                if(i + 1 < headerElements.length) {
                    writer.append(csvSep);
                } else {
                    writer.append("\n");
                }
            }
            writer.flush();
        } catch(IOException e) {
            log.error("Error while writing entity with field data: " +
                      Arrays.toString(entityFieldData.entrySet().toArray()), e);
        }
    }
}
