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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * //ToDo: Class Description
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

        BufferedWriter writer = FileIOUtils.getBufferedWriterUTF8(fullPath);

        // write header
        writeFileHeader(clz, writer, headerElements, csvSep);

        return writer;

    }

    private void writeFileHeader(Class<? extends UniqueEntity> clz,
                                 BufferedWriter writer,
                                 final String[] headerElements,
                                 String csvSep) {
        try {
            for(int i = 0; i < headerElements.length; i++) {
                String attribute = headerElements[i];
                writer.append("\"").append(attribute).append("\""); // adds " to headline
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

}
