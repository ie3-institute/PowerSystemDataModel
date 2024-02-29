/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import static java.util.Collections.emptySet;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.ThermalSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.container.ThermalGrid;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalUnitInput;
import edu.ie3.datamodel.utils.Try;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/** Convenience class for cases where all used data comes from CSV sources */
public class CsvThermalGridSource {
  private CsvThermalGridSource() {}

  /**
   * Method for reading {@link ThermalGrid}s.
   *
   * @param csvSep separator
   * @param directoryPath path to the grids
   * @return a list of thermal grids
   * @throws SourceException if an error occurs while reading the grid
   */
  public static List<ThermalGrid> read(String csvSep, Path directoryPath) throws SourceException {
    return read(csvSep, directoryPath, new FileNamingStrategy());
  }

  /**
   * Method for reading {@link ThermalGrid}s.
   *
   * @param csvSep separator
   * @param directoryPath path to the grids
   * @param fileNamingStrategy file naming strategy
   * @return a list of thermal grids
   * @throws SourceException if an error occurs while reading the grid
   */
  public static List<ThermalGrid> read(
      String csvSep, Path directoryPath, FileNamingStrategy fileNamingStrategy)
      throws SourceException {
    CsvDataSource dataSource = new CsvDataSource(csvSep, directoryPath, fileNamingStrategy);
    TypeSource typeSource = new TypeSource(dataSource);
    ThermalSource thermalSource = new ThermalSource(typeSource, dataSource);

    /* validating sources */
    try {
      typeSource.validate();
      thermalSource.validate();
    } catch (ValidationException ve) {
      throw new SourceException("Could not read source because validation failed", ve);
    }

    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, ThermalBusInput> buses = thermalSource.getThermalBuses();

    // calling Map.values() because we want to map the inputs to their thermal bus
    Try<Collection<ThermalHouseInput>, SourceException> houses =
        Try.of(
            () -> thermalSource.getThermalHouses(operators, buses).values(), SourceException.class);
    Try<Collection<ThermalStorageInput>, SourceException> storages =
        Try.of(
            () -> thermalSource.getThermalStorages(operators, buses).values(),
            SourceException.class);

    List<? extends Exception> exceptions = Try.getExceptions(houses, storages);

    if (!exceptions.isEmpty()) {
      throw new SourceException(
          exceptions.size() + " error(s) occurred while reading sources. ", exceptions);
    } else {
      // getOrThrow should not throw an exception in this context, because all exception are
      // filtered and thrown before

      Map<ThermalBusInput, Set<ThermalHouseInput>> houseInputs =
          houses.getOrThrow().stream()
              .collect(Collectors.groupingBy(ThermalUnitInput::getThermalBus, Collectors.toSet()));
      Map<ThermalBusInput, Set<ThermalStorageInput>> storageInputs =
          storages.getOrThrow().stream()
              .collect(Collectors.groupingBy(ThermalUnitInput::getThermalBus, Collectors.toSet()));

      return buses.values().stream()
          .map(
              bus -> {
                Set<ThermalHouseInput> h = houseInputs.getOrDefault(bus, emptySet());
                Set<ThermalStorageInput> s = storageInputs.getOrDefault(bus, emptySet());
                return new ThermalGrid(bus, h, s);
              })
          .toList();
    }
  }
}
