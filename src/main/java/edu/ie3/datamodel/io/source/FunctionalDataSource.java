package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.timeseries.IdCoordinateFactory;
import edu.ie3.datamodel.models.UniqueEntity;

import java.util.*;
import java.util.stream.Stream;

public interface FunctionalDataSource {

    /*
    protected static final Logger log = LoggerFactory.getLogger(FunctionalDataSource.class);

    // field names
    protected static final String OPERATOR = "operator";
    protected static final String NODE_A = "nodeA";
    protected static final String NODE_B = "nodeB";
    protected static final String NODE = "node";
    protected static final String TYPE = "type";
    protected static final String FIELDS_TO_VALUES_MAP = "fieldsToValuesMap";

    protected static final FileNamingStrategy namingStrategy = new FileNamingStrategy();

     */


    <T extends UniqueEntity> Stream<Map<String, String>> getSourceData(Class<T> entityClass);

    <T extends UniqueEntity> Stream<Map<String, String>> getSourceData(Class<T> entityClass, String specialPlace) throws SourceException;
    <T extends UniqueEntity> Stream<Map<String, String>> getSourceData(String specialPlace) throws SourceException;

    <T extends UniqueEntity> Stream<Map<String, String>> getSourceData();

    Stream<Map<String, String>> getSourceData(IdCoordinateFactory factory);
}
