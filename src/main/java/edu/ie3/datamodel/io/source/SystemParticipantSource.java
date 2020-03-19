/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.input.container.SystemParticipants;

/** Describes a data source for system participants */
public interface SystemParticipantSource extends DataSource {

  /** @return system participant data as an aggregation of all elements in this grid */
  SystemParticipants fetchSystemParticipants();
}
