/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.system.type.*;
import java.util.Set;

public interface TypeSource extends DataSource {
  // TODO

  Set<Transformer2WTypeInput> getTransformer2WTypes();

  Set<OperatorInput> getOperators();

  Set<LineTypeInput> getLineTypes();

  Set<Transformer3WTypeInput> getTransformer3WTypes();

  Set<BmTypeInput> getBmTypes();

  Set<ChpTypeInput> getChpTypes();

  Set<HpTypeInput> getHpTypes();

  Set<StorageTypeInput> getStorageTypes();

  Set<WecTypeInput> getWecTypes();

  Set<EvTypeInput> getEvTypes();
}
