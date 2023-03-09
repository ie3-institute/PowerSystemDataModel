/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.options;

import java.util.concurrent.Callable;

public interface TryTestData {
  default Callable<String> callable() {
    return () -> "test";
  }

  default Runnable runnable() {
    return () -> {
      throw new RuntimeException("Exception thrown.");
    };
  }
}
