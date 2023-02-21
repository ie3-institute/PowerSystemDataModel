/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.GraphicSourceException;
import edu.ie3.datamodel.exceptions.RawGridException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.SystemParticipantsException;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.*;
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.container.JointGridContainer;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.utils.options.Try;
import java.util.List;
import java.util.stream.Stream;

/** Convenience class for cases where all used data comes from CSV sources */
public class CsvJointGridContainerSource {
  private CsvJointGridContainerSource() {}

  public static JointGridContainer read(String gridName, String csvSep, String directoryPath)
      throws SourceException {

    /* Parameterization */

    FileNamingStrategy namingStrategy = new FileNamingStrategy(); // Default naming strategy

    /* Instantiating sources */
    TypeSource typeSource = new CsvTypeSource(csvSep, directoryPath, namingStrategy);
    RawGridSource rawGridSource =
        new CsvRawGridSource(csvSep, directoryPath, namingStrategy, typeSource);
    ThermalSource thermalSource =
        new CsvThermalSource(csvSep, directoryPath, namingStrategy, typeSource);
    SystemParticipantSource systemParticipantSource =
        new CsvSystemParticipantSource(
            csvSep, directoryPath, namingStrategy, typeSource, thermalSource, rawGridSource);
    GraphicSource graphicsSource =
        new CsvGraphicSource(csvSep, directoryPath, namingStrategy, typeSource, rawGridSource);

    /* Loading models */
    Try<RawGridElements, RawGridException> rawGridElements = Try.apply(rawGridSource::getGridData);
    Try<SystemParticipants, SystemParticipantsException> systemParticipants =
        Try.apply(systemParticipantSource::getSystemParticipants);
    Try<GraphicElements, GraphicSourceException> graphicElements =
        Try.apply(graphicsSource::getGraphicElements);

    List<? extends Exception> exceptions =
        Stream.of(rawGridElements, systemParticipants, graphicElements)
            .filter(Try::isFailure)
            .map(Try::getException)
            .toList();

    if (exceptions.size() > 0) {
      throw new SourceException(
          exceptions.size() + " error(s) occurred while reading sources. ", exceptions);
    } else {
      return new JointGridContainer(
          gridName,
          rawGridElements.getData(),
          systemParticipants.getData(),
          graphicElements.getData());
    }
  }
}
