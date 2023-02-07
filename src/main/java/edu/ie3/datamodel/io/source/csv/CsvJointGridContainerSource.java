/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.*;
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.container.JointGridContainer;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.models.input.container.SystemParticipants;

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
    RawGridElements rawGridElements =
        rawGridSource
            .getGridData()
            .orElseThrow(() -> new SourceException("Error during reading of raw grid data."));
    SystemParticipants systemParticipants =
        systemParticipantSource
            .getSystemParticipants()
            .orElseThrow(
                () -> new SourceException("Error during reading of system participant data."));
    GraphicElements graphicElements =
        graphicsSource
            .getGraphicElements()
            .orElseThrow(() -> new SourceException("Error during reading of graphic elements."));
    JointGridContainer fullGrid =
        new JointGridContainer(gridName, rawGridElements, systemParticipants, graphicElements);

    return fullGrid;
  }
}
