/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.FileException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.naming.DefaultDirectoryHierarchy;
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.*;
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.container.JointGridContainer;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.utils.Try;
import java.nio.file.Path;
import java.util.List;

/** Convenience class for cases where all used data comes from CSV sources */
public class CsvJointGridContainerSource {
  private CsvJointGridContainerSource() {}

  public static JointGridContainer read(
      String gridName, String csvSep, Path directoryPath, boolean isHierarchic)
      throws SourceException, FileException {

    /* Parameterization */
    FileNamingStrategy namingStrategy;

    if (isHierarchic) {
      // Hierarchic structure
      DefaultDirectoryHierarchy fileHierarchy =
          new DefaultDirectoryHierarchy(directoryPath, gridName);
      namingStrategy = new FileNamingStrategy(new EntityPersistenceNamingStrategy(), fileHierarchy);
      fileHierarchy.validate();
    } else {
      // Flat structure
      namingStrategy = new FileNamingStrategy();
    }

    CsvDataSource dataSource = new CsvDataSource(csvSep, directoryPath, namingStrategy);

    /* Instantiating sources */
    TypeSource typeSource = new TypeSource(dataSource);
    RawGridSource rawGridSource = new RawGridSource(typeSource, dataSource);
    ThermalSource thermalSource = new ThermalSource(typeSource, dataSource);
    SystemParticipantSource systemParticipantSource =
        new SystemParticipantSource(typeSource, thermalSource, rawGridSource, dataSource);
    GraphicSource graphicSource = new GraphicSource(typeSource, rawGridSource, dataSource);

    /* Loading models */
    Try<RawGridElements> rawGridElements = Try.of(rawGridSource::getGridData);
    Try<SystemParticipants> systemParticipants =
        Try.of(systemParticipantSource::getSystemParticipants);
    Try<GraphicElements> graphicElements = Try.of(graphicSource::getGraphicElements);

    List<? extends Exception> exceptions =
        Try.getExceptions(rawGridElements, systemParticipants, graphicElements);

    if (exceptions.size() > 0) {
      throw new SourceException(
          exceptions.size() + " error(s) occurred while reading sources. ", exceptions);
    } else {
      return new JointGridContainer(
          gridName,
          rawGridElements.getData().get(),
          systemParticipants.getData().get(),
          graphicElements.getData().get());
    }
  }
}
