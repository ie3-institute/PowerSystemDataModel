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
import java.util.Collection;

public interface TypeSource extends DataSource {
  // TODO

  Collection<Transformer2WTypeInput> getTransformer2WTypes();

  Collection<OperatorInput> getOperators();

  Collection<LineTypeInput> getLineTypes();

  Collection<Transformer3WTypeInput> getTransformer3WTypes();

  Collection<BmTypeInput> getBmTypes();

  Collection<ChpTypeInput> getChpTypes();

  Collection<HpTypeInput> getHpTypes();

  Collection<StorageTypeInput> getStorageTypes();

  Collection<WecTypeInput> getWecTypes();

  Collection<EvTypeInput> getEvTypes();
}
