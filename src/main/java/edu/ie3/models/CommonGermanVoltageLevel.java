/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models;

import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLT;

import edu.ie3.exceptions.VoltageLevelException;
import edu.ie3.util.interval.RightOpenInterval;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.measure.quantity.ElectricPotential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

public enum CommonGermanVoltageLevel implements CommonVoltageLevel {
  LV(
      "Niederspannung",
      new HashSet<>(Arrays.asList("lv", "ns")),
      new RightOpenInterval<>(
          Quantities.getQuantity(0d, KILOVOLT), Quantities.getQuantity(10d, KILOVOLT)),
      Quantities.getQuantity(0.4, KILOVOLT)),
  MV_10KV(
      "Mittelspannung (10 kV)",
      new HashSet<>(Arrays.asList("ms", "mv", "ms_10kv", "mv_10kV")),
      new RightOpenInterval<>(
          Quantities.getQuantity(10d, KILOVOLT), Quantities.getQuantity(20d, KILOVOLT)),
      Quantities.getQuantity(10d, KILOVOLT)),
  MV_20KV(
      "Mittelspannung (20 kV)",
      new HashSet<>(Arrays.asList("ms", "mv", "ms_20kv", "mv_20kV")),
      new RightOpenInterval<>(
          Quantities.getQuantity(20d, KILOVOLT), Quantities.getQuantity(30d, KILOVOLT)),
      Quantities.getQuantity(20d, KILOVOLT)),
  MV_30KV(
      "Mittelspannung (30 kV)",
      new HashSet<>(Arrays.asList("ms", "mv", "ms_30kv", "mv_30kV")),
      new RightOpenInterval<>(
          Quantities.getQuantity(30d, KILOVOLT), Quantities.getQuantity(110d, KILOVOLT)),
      Quantities.getQuantity(30d, KILOVOLT)),
  HV(
      "Hochspannung",
      new HashSet<>(Arrays.asList("hs", "hv")),
      new RightOpenInterval<>(
          Quantities.getQuantity(110d, KILOVOLT), Quantities.getQuantity(220d, KILOVOLT)),
      Quantities.getQuantity(110d, KILOVOLT)),
  EHV_220KV(
      "Höchstspannung (220 kV)",
      new HashSet<>(Arrays.asList("hoes", "ehv", "hoes_220kv", "ehv_220kv")),
      new RightOpenInterval<>(
          Quantities.getQuantity(220d, KILOVOLT), Quantities.getQuantity(380d, KILOVOLT)),
      Quantities.getQuantity(220d, KILOVOLT)),
  EHV_380KV(
      "Höchstspannung (380 kV)",
      new HashSet<>(Arrays.asList("hoes", "ehv", "hoes_380kv", "ehv_380kv")),
      new RightOpenInterval<>(
          Quantities.getQuantity(380d, KILOVOLT), Quantities.getQuantity(560d, KILOVOLT)),
      Quantities.getQuantity(380d, KILOVOLT));

  private static final Logger logger = LoggerFactory.getLogger(CommonVoltageLevel.class);
  private String id;
  private Set<String> synonymousIds;
  private RightOpenInterval<ComparableQuantity<ElectricPotential>> voltageRange;
  private ComparableQuantity<ElectricPotential> nominalVoltage;

  CommonGermanVoltageLevel(
      String id,
      Set<String> synonymousIds,
      RightOpenInterval<ComparableQuantity<ElectricPotential>> voltageRange,
      ComparableQuantity<ElectricPotential> nominalVoltage) {
    this.id = id;
    this.synonymousIds = synonymousIds;
    this.voltageRange = voltageRange;
    this.nominalVoltage = nominalVoltage;
  }

  /**
   * Parses the given rated voltage and returns a suitable german voltage level, unless it is not
   * covered by any of the given. Then a {@link VoltageLevelException} is thrown.
   *
   * @param vRated Rated voltage to examine
   * @return A suitable voltage level
   */
  public static CommonVoltageLevel parse(ComparableQuantity<ElectricPotential> vRated)
      throws VoltageLevelException {
    return Arrays.stream(values())
        .filter(
            voltLvl -> {
              try {
                return voltLvl.covers(vRated);
              } catch (VoltageLevelException e) {
                logger.debug(
                    "Found some potentially inconsistent voltage level information: {}",
                    e.getMessage());
                return false;
              }
            })
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
   * @param vRated Rated voltage to examine
   * @return A suitable voltage level
   */
  public static CommonVoltageLevel parse(String id, ComparableQuantity<ElectricPotential> vRated)
      throws VoltageLevelException {
    return Arrays.stream(values())
        .filter(
            voltLvl -> {
              try {
                return voltLvl.covers(id, vRated);
              } catch (VoltageLevelException e) {
                logger.debug(
                    "Found some potentially inconsistent voltage level information: {}",
                    e.getMessage());
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

  @Override
  public String getId() {
    return id;
  }

  @Override
  public Set<String> getSynonymousIds() {
    return synonymousIds;
  }

  @Override
  public RightOpenInterval<ComparableQuantity<ElectricPotential>> getRatedVoltageRange() {
    return voltageRange;
  }

  @Override
  public ComparableQuantity<ElectricPotential> getNominalVoltage() {
    return nominalVoltage;
  }

  @Override
  public boolean covers(ComparableQuantity<ElectricPotential> vRated) throws VoltageLevelException {
    return voltageRange.includes(vRated);
  }

  /**
   * Checks, whether the given tuple of identifier and rated voltage is covered. If one of both
   * criteria is met, an {@link IllegalArgumentException} is thrown, as the given information then
   * is supposed to be corrupt.
   *
   * @param id Identifier
   * @param vRated Rated voltage of a node to test
   * @return true, if it is covered
   */
  @Override
  public boolean covers(String id, ComparableQuantity<ElectricPotential> vRated)
      throws VoltageLevelException {
    boolean idCovered = synonymousIds.contains(id.toLowerCase());
    boolean voltageCovered = covers(vRated);

    if (idCovered ^ voltageCovered)
      throw new VoltageLevelException(
          "The provided id \""
              + id
              + "\" and rated voltage \""
              + vRated
              + "\" could possibly meet the voltage level \""
              + this.id
              + "\" ("
              + getRatedVoltageRange()
              + "), but are inconsistent.");
    return idCovered; /* voltage covered is always true, otherwise the exception would have been thrown. */
  }
}
