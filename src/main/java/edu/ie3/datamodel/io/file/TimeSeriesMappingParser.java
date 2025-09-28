/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.file;

import edu.ie3.datamodel.exceptions.SourceException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface TimeSeriesMappingParser {
  Stream<Map<String, String>> parse() throws SourceException;

  Optional<Set<String>> availableFields() throws SourceException;
}
