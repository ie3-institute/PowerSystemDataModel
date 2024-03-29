/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.voltagelevels;

import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLT;

import edu.ie3.datamodel.exceptions.VoltageLevelException;
import edu.ie3.util.interval.RightOpenInterval;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.measure.quantity.ElectricPotential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public class GermanVoltageLevelUtils {
  protected static final Logger logger = LoggerFactory.getLogger(GermanVoltageLevelUtils.class);

  private static final String MS = "Mittelspannung";

  public static final CommonVoltageLevel LV =
      new CommonVoltageLevel(
          "Niederspannung",
          Quantities.getQuantity(0.4, KILOVOLT),
          new HashSet<>(Arrays.asList("lv", "ns")),
          new RightOpenInterval<>(
              Quantities.getQuantity(0d, KILOVOLT), Quantities.getQuantity(10d, KILOVOLT)));
  public static final CommonVoltageLevel MV_10KV =
      new CommonVoltageLevel(
          MS,
          Quantities.getQuantity(10d, KILOVOLT),
          new HashSet<>(Arrays.asList("ms", "mv", "ms_10kv", "mv_10kV")),
          new RightOpenInterval<>(
              Quantities.getQuantity(10d, KILOVOLT), Quantities.getQuantity(20d, KILOVOLT)));
  public static final CommonVoltageLevel MV_20KV =
      new CommonVoltageLevel(
          MS,
          Quantities.getQuantity(20d, KILOVOLT),
          new HashSet<>(Arrays.asList("ms", "mv", "ms_20kv", "mv_20kV")),
          new RightOpenInterval<>(
              Quantities.getQuantity(20d, KILOVOLT), Quantities.getQuantity(30d, KILOVOLT)));
  public static final CommonVoltageLevel MV_30KV =
      new CommonVoltageLevel(
          MS,
          Quantities.getQuantity(30d, KILOVOLT),
          new HashSet<>(Arrays.asList("ms", "mv", "ms_30kv", "mv_30kV")),
          new RightOpenInterval<>(
              Quantities.getQuantity(30d, KILOVOLT), Quantities.getQuantity(110d, KILOVOLT)));
  public static final CommonVoltageLevel HV =
      new CommonVoltageLevel(
          "Hochspannung",
          Quantities.getQuantity(110d, KILOVOLT),
          new HashSet<>(Arrays.asList("hs", "hv")),
          new RightOpenInterval<>(
              Quantities.getQuantity(110d, KILOVOLT), Quantities.getQuantity(220d, KILOVOLT)));
  public static final CommonVoltageLevel EHV_220KV =
      new CommonVoltageLevel(
          "Höchstspannung",
          Quantities.getQuantity(220d, KILOVOLT),
          new HashSet<>(Arrays.asList("hoes", "ehv", "hoes_220kv", "ehv_220kv")),
          new RightOpenInterval<>(
              Quantities.getQuantity(220d, KILOVOLT), Quantities.getQuantity(380d, KILOVOLT)));
  public static final CommonVoltageLevel EHV_380KV =
      new CommonVoltageLevel(
          "Höchstspannung",
          Quantities.getQuantity(380d, KILOVOLT),
          new HashSet<>(Arrays.asList("hoes", "ehv", "hoes_380kv", "ehv_380kv")),
          new RightOpenInterval<>(
              Quantities.getQuantity(380d, KILOVOLT), Quantities.getQuantity(560d, KILOVOLT)));

  private static final Set<CommonVoltageLevel> germanVoltageLevels =
      Set.of(LV, MV_10KV, MV_20KV, MV_30KV, HV, EHV_220KV, EHV_380KV);

  private GermanVoltageLevelUtils() {
    throw new IllegalStateException("This is a factory class. Don't try to instantiate it.");
  }

  public static Set<CommonVoltageLevel> getGermanVoltageLevels() {
    return germanVoltageLevels;
  }

  /**
   * Parses the given rated voltage and returns a suitable german voltage level, unless it is not
   * covered by any of the given. Then a {@link VoltageLevelException} is thrown.
   *
   * @param vRated Rated voltage to examine
   * @return A suitable voltage level
   * @throws VoltageLevelException If the given rated voltage is not covered by any of the known
   *     voltage levels
   */
  public static CommonVoltageLevel parse(ComparableQuantity<ElectricPotential> vRated)
      throws VoltageLevelException {
    return germanVoltageLevels.stream()
        .filter(voltLvl -> voltLvl.covers(vRated))
        .findFirst()
        .orElseThrow(
            () ->
                new VoltageLevelException(
                    "The rated voltage "
                        + vRated
                        + " is not covered by none of the commonly known german voltage levels."));
  }

  /**
   * Parses the given id and rated voltage and returns a suitable german voltage level, unless it is
   * not covered by any of the given. Then a {@link VoltageLevelException} is thrown.
   *
   * @param id Identifier of the voltage level
   * @param vRated Rated voltage to examine
   * @return A suitable voltage level
   * @throws VoltageLevelException If the given rated voltage is not covered by any of the known
   *     voltage levels
   */
  public static CommonVoltageLevel parse(String id, ComparableQuantity<ElectricPotential> vRated)
      throws VoltageLevelException {
    return germanVoltageLevels.stream()
        .filter(
            voltLvl -> {
              try {
                return voltLvl.covers(id, vRated);
              } catch (VoltageLevelException e) {
                return false;
              }
            })
        .findFirst()
        .orElseThrow(
            () ->
                new VoltageLevelException(
                    "The id "
                        + id
                        + " in combination with the rated voltage "
                        + vRated
                        + " is not covered by none of the commonly known german voltage levels."));
  }
}
