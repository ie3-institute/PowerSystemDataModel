/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.metrics;

import edu.ie3.models.StandardUnits;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.ElectricPotential;
import tec.uom.se.quantity.Quantities;

public class TestUnequality {
  private static final int numberOfEntities = 3;
  private static final UUID comparisonUUID =
      UUID.fromString("11111111-2222-3333-4444-555555555555");
  private static int index = 0;
  private static UUID[] uuids = new UUID[numberOfEntities];

  static {
    for (int i = 0; i < numberOfEntities; i++) {
      uuids[i] = UUID.randomUUID();
    }
  }

  public static void main(String[] args) {
    Quantity<ElectricPotential> quantityA = Quantities.getQuantity(10, StandardUnits.V_RATED);
    Quantity<ElectricPotential> quantityB = Quantities.getQuantity(10.0, StandardUnits.V_RATED);

    System.out.println("A.hashCode() ? " + quantityA.hashCode()); // 1104230
    System.out.println("B.hashCode() ? " + quantityB.hashCode()); // 1077205340
    System.out.println(
        "A.hashCode() ==  B.hashCode() ? "
            + (quantityA.hashCode() == quantityB.hashCode())); // false

    System.out.println("A.equals(B) ? " + quantityA.equals(quantityB)); // true
    System.out.println("B.equals(A) ? " + quantityB.equals(quantityA)); // true

    System.out.println("Objects.equals(A, B) ? " + Objects.equals(quantityA, quantityB)); // true
    System.out.println("Objects.equals(B, A) ? " + Objects.equals(quantityB, quantityA)); // true

    HashSet<Quantity<ElectricPotential>> quantitySet = new HashSet<>();
    quantitySet.add(quantityA);
    System.out.println("Set.contains(A) ? " + quantitySet.contains(quantityA)); // true
    System.out.println("Set.contains(B) ? " + quantitySet.contains(quantityB)); // false

    System.out.println(Arrays.stream(uuids).distinct().count());
  }
}
