package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.timeseries.IdCoordinateFactory;
import edu.ie3.datamodel.models.UniqueEntity;

import java.util.*;
import java.util.stream.Stream;

/**
 * Interface that include functionalities for data sources
 */
public interface FunctionalDataSource {

    /**
     * Creates a stream of maps that represent the rows in the database
     */
    Stream<Map<String, String>> getSourceData(Class<? extends UniqueEntity> entityClass);

    /**
     * Creates a stream of maps that represent the rows in the database from a explicit path or table.
     */
    Stream<Map<String, String>> getSourceData(Class<? extends UniqueEntity> entityClass, String explicitPlace) throws SourceException;

    /**
     * Creates a stream of maps that represent the rows in the database for IdCoordinates
     */
    Stream<Map<String, String>> getIdCoordinateSourceData(IdCoordinateFactory factory);
}
