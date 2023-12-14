/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.FileException;
import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.naming.DefaultDirectoryHierarchy;
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.*;
import edu.ie3.datamodel.models.input.container.*;
import edu.ie3.datamodel.utils.Try;
import java.nio.file.Path;
import java.util.*;

/** Convenience class for cases where all used data comes from CSV sources */
public class CsvJointGridContainerSource {
  private CsvJointGridContainerSource() {}

  public static JointGridContainer read(
      String gridName, String csvSep, Path directoryPath, boolean isHierarchic)
      throws SourceException, FileException, InvalidGridException {

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
    EnergyManagementSource emSource = new EnergyManagementSource(typeSource, dataSource);
    GraphicSource graphicSource = new GraphicSource(typeSource, rawGridSource, dataSource);

    /* validating sources */
    typeSource.validate();
    rawGridSource.validate();
    systemParticipantSource.validate();
    graphicSource.validate();

    /* Loading models */
    Try<RawGridElements, SourceException> rawGridElements =
        Try.of(rawGridSource::getGridData, SourceException.class);
    Try<SystemParticipants, SourceException> systemParticipants =
        Try.of(systemParticipantSource::getSystemParticipants, SourceException.class);
    Try<EnergyManagementUnits, SourceException> emUnits =
        Try.of(emSource::getEmUnits, SourceException.class);
    Try<GraphicElements, SourceException> graphicElements =
        Try.of(graphicSource::getGraphicElements, SourceException.class);

    List<? extends Exception> exceptions =
        Try.getExceptions(List.of(rawGridElements, systemParticipants, graphicElements));

    if (!exceptions.isEmpty()) {
      throw new SourceException(
          exceptions.size() + " error(s) occurred while reading sources. ", exceptions);
    } else {
      // getOrThrow should not throw an exception in this context, because all exception are
      // filtered and thrown before
      return new JointGridContainer(
          gridName,
          rawGridElements.getOrThrow(),
          systemParticipants.getOrThrow(),
          emUnits.getOrThrow(),
          graphicElements.getOrThrow());
    }
  }
}
