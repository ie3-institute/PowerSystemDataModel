/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor;

import java.util.SequencedMap;

public interface Processable {

  /** Returns a map: field name to attribute. */
  SequencedMap<String, String> toMap();
}
