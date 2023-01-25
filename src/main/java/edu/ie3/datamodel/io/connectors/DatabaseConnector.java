package edu.ie3.datamodel.io.connectors;

import edu.ie3.datamodel.io.csv.timeseries.IndividualTimeSeriesMetaInformation;

import java.util.Optional;
import java.util.UUID;

public interface DatabaseConnector extends DataConnector {

    Optional<IndividualTimeSeriesMetaInformation> getIndividualTimeSeriesMetaInformation(UUID timeSeriesUuid);


}
