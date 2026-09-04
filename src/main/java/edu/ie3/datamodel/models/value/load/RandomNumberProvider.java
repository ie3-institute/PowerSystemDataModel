/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value.load;

import de.lmu.ifi.dbs.elki.utilities.random.RandomFactory;
import java.util.concurrent.ThreadLocalRandom;

/** Interface that provides a random factory. */
public interface RandomNumberProvider {
  RandomFactory factory = RandomFactory.get(ThreadLocalRandom.current().nextLong());
}
