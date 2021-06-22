/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.result.NodeResult;
import edu.ie3.datamodel.models.result.connector.LineResult;
import edu.ie3.datamodel.models.result.connector.SwitchResult;
import edu.ie3.datamodel.models.result.connector.Transformer2WResult;
import edu.ie3.datamodel.models.result.connector.Transformer3WResult;
import edu.ie3.datamodel.models.result.system.*;
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult;
import edu.ie3.datamodel.models.result.thermal.ThermalHouseResult;
import java.util.Set;

public interface ResultEntitySource {

  // Grid
  Set<NodeResult> getNodeResults();

  Set<SwitchResult> getSwitchResults();

  Set<LineResult> getLineResults();

  Set<Transformer2WResult> getTransformer2WResultResults();

  Set<Transformer3WResult> getTransformer3WResultResults();

  // System Participants
  Set<LoadResult> getLoadResults();

  Set<PvResult> getPvResults();

  Set<FixedFeedInResult> getFixedFeedInResults();

  Set<BmResult> getBmResults();

  Set<ChpResult> getChpResults();

  Set<WecResult> getWecResults();

  Set<StorageResult> getStorageResults();

  Set<EvcsResult> getEvcsResults();

  Set<EvResult> getEvResults();

  Set<HpResult> getHpResults();

  Set<CylindricalStorageResult> getCylindricalStorageResult();

  Set<ThermalHouseResult> getThermalHouseResults();
}
