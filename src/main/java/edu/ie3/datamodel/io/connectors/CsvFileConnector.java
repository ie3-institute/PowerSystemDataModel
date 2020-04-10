/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.connectors;

import edu.ie3.datamodel.exceptions.ConnectorException;
import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.util.io.FileIOUtils;

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static edu.ie3.util.io.FileIOUtils.CHARSET_UTF8;


/**
 * Provides the connector (here: buffered writer) for specific files to be used by a {@link
 * edu.ie3.datamodel.io.sink.CsvFileSink}
 *
 * @version 0.1
 * @since 19.03.20
 */
public class CsvFileConnector implements DataConnector {

    private static final Logger log = LogManager.getLogger(CsvFileConnector.class);

    private final Map<Class<? extends UniqueEntity>, BufferedWriter> writers = new HashMap<>();
    private final FileNamingStrategy                                 fileNamingStrategy;
    private final String                                             baseFolderName;

    private static final String FILE_ENDING = ".csv";

    public CsvFileConnector(String baseFolderName, FileNamingStrategy fileNamingStrategy) {
        this.baseFolderName = baseFolderName;
        this.fileNamingStrategy = fileNamingStrategy;
    }

    @Override
    public void shutdown() {

        writers.values().forEach(bufferedWriter -> {
            try {
                bufferedWriter.close();
            } catch(IOException e) {
                log.error("Error during CsvFileConnector shutdown process.", e);
            }
        });
    }

    public BufferedWriter initWriter(Class<? extends UniqueEntity> clz, String[] headerElements, String csvSep) throws
                    ConnectorException,
                    IOException {
        return initWriter(baseFolderName, clz, fileNamingStrategy, headerElements, csvSep);
    }

    public Optional<BufferedWriter> getWriter(Class<? extends UniqueEntity> clz) {
        return Optional.ofNullable(writers.get(clz));
    }

    public BufferedWriter getOrInitWriter(Class<? extends UniqueEntity> clz, String[] headerElements, String csvSep) {

        return getWriter(clz).orElseGet(() -> {
            BufferedWriter newWriter = null;
            try {
                newWriter = initWriter(clz, headerElements, csvSep);
            } catch(ConnectorException | IOException e) {
                log.error("Error while initiating writer in CsvFileConnector.", e);
            }

            writers.put(clz, newWriter);
            return newWriter;
        });
    }

    private BufferedWriter initWriter(String baseFolderName,
                                      Class<? extends UniqueEntity> clz,
                                      FileNamingStrategy fileNamingStrategy,
                                      String[] headerElements,
                                      String csvSep) throws ConnectorException, IOException {
        File basePathDir = new File(baseFolderName);
        if(basePathDir.isFile())
            throw new ConnectorException("Base path dir '" + baseFolderName + "' already exists and is a file!");
        if(!basePathDir.exists())
            basePathDir.mkdirs();

        String fileName = fileNamingStrategy.getFileName(clz).orElseThrow(() -> new ConnectorException(
                        "Cannot determine the file name for provided class '" + clz.getSimpleName() + "'."));
        String fullPath = baseFolderName + File.separator + fileName + FILE_ENDING;

        File pathFile = new File(fullPath);

        if(!pathFile.exists()) {
            BufferedWriter writer = FileIOUtils.getBufferedWriter(fullPath, CHARSET_UTF8, true);
            // write header
            writeFileHeader(clz, writer, prepareHeader(headerElements), csvSep);
            return writer;
        }

        log.warn("File '{}{}' already exist. Will append new content WITHOUT new header! Full path: {}", fileName,
                        FILE_ENDING, pathFile.getAbsolutePath());

        return FileIOUtils.getBufferedWriter(fullPath, CHARSET_UTF8, true);

    }

    /**
     * Prepares the header to be written out. In our case this means adding double quotes at the
     * beginning and end of each header element as well as transforming the header element to snake
     * case to allow for database compatibility
     *
     * @param headerElements the header elements that should be written out
     * @return ready to be written header elements
     */
    private String[] prepareHeader(final String[] headerElements) {
        // adds " to headline + transforms camel case to snake case
        return Arrays.stream(headerElements)
                        .map(headerElement -> "\"" + camelCaseToSnakeCase(headerElement).concat("\""))
                        .toArray(String[]::new);
    }

    private void writeFileHeader(Class<? extends UniqueEntity> clz,
                                 BufferedWriter writer,
                                 final String[] headerElements,
                                 String csvSep) {
        try {
            for(int i = 0; i < headerElements.length; i++) {
                String attribute = headerElements[i];
                writer.append(attribute);
                if(i + 1 < headerElements.length) {
                    writer.append(csvSep);
                } else {
                    writer.append("\n");
                }
            }
            writer.flush();
        } catch(IOException e) {
            log.error("Error during file header creation for class '" + clz.getSimpleName() + "'.", e);
        }
    }

    public BufferedReader getReader(Class<? extends UniqueEntity> clz) throws FileNotFoundException {

        BufferedReader newReader = null;

        String fileName = null;
        try {
            fileName = fileNamingStrategy.getFileName(clz).orElseThrow(() -> new ConnectorException(
                            "Cannot find a naming strategy for class '" + clz.getSimpleName() + "'."));
        } catch(ConnectorException e) {
            log.error("Cannot get reader for entity '{}' as no file naming strategy for this file exists. Exception: {}",
                            clz.getSimpleName(), e);
        }
        File filePath = new File(baseFolderName + File.separator + fileName + FILE_ENDING);
        newReader = new BufferedReader(new FileReader(filePath), 16384);

        return newReader;
    }

    /**
     * Converts a given camel case string to its snake case representation
     *
     * @param camelCaseString the camel case string
     * @return the resulting snake case representation
     */
    private String camelCaseToSnakeCase(String camelCaseString) {
        String regularCamelCaseRegex = "([a-z])([A-Z]+)";
        String regularSnakeCaseReplacement = "$1_$2";
        String specialCamelCaseRegex = "((?<!_)[A-Z]?)((?<!^)[A-Z]+)";
        String specialSnakeCaseReplacement = "$1_$2";
        return camelCaseString.replaceAll(regularCamelCaseRegex, regularSnakeCaseReplacement)
                        .replaceAll(specialCamelCaseRegex, specialSnakeCaseReplacement).toLowerCase();
    }
}
