/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type.chargingpoint;

import static edu.ie3.datamodel.models.ElectricCurrentType.AC;
import static edu.ie3.datamodel.models.ElectricCurrentType.DC;

import edu.ie3.datamodel.exceptions.ChargingPointTypeException;
import edu.ie3.datamodel.models.ElectricCurrentType;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.StringUtils;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

/**
 * Utility class to create either custom instances of {@link ChargingPointType}s or to retrieve
 * common standard {@link ChargingPointType}s based on their id
 *
 * @version 0.1
 * @since 26.07.20
 */
public class ChargingPointTypeUtils {

  private ChargingPointTypeUtils() {
    throw new IllegalStateException("This is a factory class. Don't try to instantiate it.");
  }

  /* common charging point socket type implementations */

  public static final ChargingPointType HouseholdSocket =
      new ChargingPointType(
          "HouseholdSocket",
          Quantities.getQuantity(2.3, StandardUnits.S_RATED),
          AC,
          new HashSet<>(Arrays.asList("household", "hhs", "schuko-simple")));

  public static final ChargingPointType BlueHouseholdSocket =
      new ChargingPointType(
          "BlueHouseholdSocket",
          Quantities.getQuantity(3.6, StandardUnits.S_RATED),
          AC,
          new HashSet<>(Arrays.asList("bluehousehold", "bhs", "schuko-camping")));

  public static final ChargingPointType Cee16ASocket =
      new ChargingPointType(
          "Cee16ASocket",
          Quantities.getQuantity(11, StandardUnits.S_RATED),
          AC,
          Collections.singleton("cee16"));

  public static final ChargingPointType Cee32ASocket =
      new ChargingPointType(
          "Cee32ASocket",
          Quantities.getQuantity(22, StandardUnits.S_RATED),
          AC,
          Collections.singleton("cee32"));

  public static final ChargingPointType Cee63ASocket =
      new ChargingPointType(
          "Cee63ASocket",
          Quantities.getQuantity(43, StandardUnits.S_RATED),
          AC,
          Collections.singleton("cee63"));

  public static final ChargingPointType ChargingStationType1 =
      new ChargingPointType(
          "ChargingStationType1",
          Quantities.getQuantity(7.2, StandardUnits.S_RATED),
          AC,
          new HashSet<>(Arrays.asList("cst1", "stationtype1", "cstype1")));

  public static final ChargingPointType ChargingStationType2 =
      new ChargingPointType(
          "ChargingStationType2",
          Quantities.getQuantity(43, StandardUnits.S_RATED),
          AC,
          new HashSet<>(Arrays.asList("cst2", "stationtype2", "cstype2")));

  public static final ChargingPointType ChargingStationCcsComboType1 =
      new ChargingPointType(
          "ChargingStationCcsComboType1",
          Quantities.getQuantity(11, StandardUnits.S_RATED),
          DC,
          new HashSet<>(Arrays.asList("csccs1", "csccscombo1")));

  public static final ChargingPointType ChargingStationCcsComboType2 =
      new ChargingPointType(
          "ChargingStationCcsComboType2",
          Quantities.getQuantity(50, StandardUnits.S_RATED),
          DC,
          new HashSet<>(Arrays.asList("csccs2", "csccscombo2")));

  public static final ChargingPointType TeslaSuperChargerV1 =
      new ChargingPointType(
          "TeslaSuperChargerV1",
          Quantities.getQuantity(135, StandardUnits.S_RATED),
          DC,
          new HashSet<>(Arrays.asList("tesla1", "teslav1", "supercharger1", "supercharger")));

  public static final ChargingPointType TeslaSuperChargerV2 =
      new ChargingPointType(
          "TeslaSuperChargerV2",
          Quantities.getQuantity(150, StandardUnits.S_RATED),
          DC,
          new HashSet<>(Arrays.asList("tesla2", "teslav2", "supercharger2")));

  public static final ChargingPointType TeslaSuperChargerV3 =
      new ChargingPointType(
          "TeslaSuperChargerV3",
          Quantities.getQuantity(250, StandardUnits.S_RATED),
          DC,
          new HashSet<>(Arrays.asList("tesla3", "teslav3", "supercharger3")));

  /** all common charging point types accessible mapped on their id and all synonymous ids */
  protected static final Map<String, ChargingPointType> commonChargingPointTypes =
      Collections.unmodifiableMap(
          Stream.of(
                  HouseholdSocket,
                  BlueHouseholdSocket,
                  Cee16ASocket,
                  Cee32ASocket,
                  Cee63ASocket,
                  ChargingStationType1,
                  ChargingStationType2,
                  ChargingStationCcsComboType1,
                  ChargingStationCcsComboType2,
                  TeslaSuperChargerV1,
                  TeslaSuperChargerV2,
                  TeslaSuperChargerV3)
              .flatMap(
                  type ->
                      Stream.concat(
                          Stream.of(type.getId().toLowerCase()),
                          type.getSynonymousIds().stream().map(String::toLowerCase))
                          .collect(Collectors.toMap(Function.identity(), v -> type))
                          .entrySet()
                          .stream())
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

  /**
   * Parse a given string into either a custom {@link ChargingPointType} or a standard {@link
   * ChargingPointType}. A custom charging point type can be created if the prsable string matches
   * the following regex {@code (\w+\d*)\s*\(\s*(\d+\.?\d+)\s*\|\s*(AC|DC)\s*\)} e.g. {@code
   * FastCharger(50|DC)} is a valid custom charging point definition. The apparent power value is
   * expected to be in kVA.
   *
   * <p>For all available common standard charging point types see {@link
   * #commonChargingPointTypes}.
   *
   * @param parsableString the string to be parsed. Either a valid custom string or the id of a
   *     common standard charging point
   * @return instance of a {@link ChargingPointType}
   * @throws ChargingPointTypeException if the string is neither a valid custom charging point
   *     string nor a valid common standard charging point id
   */
  public static ChargingPointType parse(String parsableString) throws ChargingPointTypeException {

    ChargingPointType res;

    // valid regex for either custom or pre-defined types
    String validCustomRegex = "(\\w+\\d*)\\s*\\(\\s*(\\d+\\.?\\d+)\\s*\\|\\s*(AC|DC)\\s*\\)";

    // does it match the valid regex?
    Pattern pattern = Pattern.compile(validCustomRegex);
    Matcher matcher = pattern.matcher(parsableString);
    if (matcher.find()
        && matcher.groupCount()
            == 3) { // only valid if 3 groups are present (id, sRated, electric current)
      String id = matcher.group(1);

      // try to parse sRated + currentType
      // regex limits to digits -> no need to catch any NumberFormatException ||
      // NullPointerException
      final ComparableQuantity<Power> sRated =
          Quantities.getQuantity(Double.parseDouble(matcher.group(2)), StandardUnits.S_RATED);

      // regex limits to AC|DC -> parsing must always succeed, exception for safety reasons only
      String currentTypeString = matcher.group(3);
      ElectricCurrentType currentType =
          ElectricCurrentType.parse(currentTypeString)
              .orElseThrow(
                  () ->
                      new ChargingPointTypeException(
                          "Cannot parse '"
                              + parsableString
                              + "' to charging point: "
                              + "Invalid electric current type value '"
                              + currentTypeString
                              + "' provided!"));

      // search for common type that is equal to the input or build a custom one
      res =
          fromIdString(id)
              .flatMap(
                  commonType -> {
                    if (commonType.getsRated().equals(sRated)
                        && commonType.getElectricCurrentType().equals(currentType)) {
                      return Optional.of(commonType);
                    } else {
                      return Optional.empty();
                    }
                  })
              .orElseGet(() -> new ChargingPointType(id, sRated, currentType));
    } else {
      // is it only the name of a pre-defined type?
      res =
          fromIdString(parsableString)
              .orElseThrow(
                  () ->
                      new ChargingPointTypeException(
                          "Provided charging point type string '"
                              + parsableString
                              + "' is neither a valid custom type string "
                              + "nor can a common charging point type with id '"
                              + parsableString
                              + "' be found! "
                              + "Please either provide a valid custom string in the format '<Name>(<kVA Value>|<AC|DC>)' "
                              + "(e.g. 'FastCharger(50|DC)') or a common type id (see docs for all available common types)."));
    }

    return res;
  }

  /**
   * Retrieve a common standard charging point type based on its id or one of its synonymous ids
   *
   * <p>For all available common standard charging point types see {@link
   * #commonChargingPointTypes}.
   *
   * @param id the id of the common standard charging point type
   * @return optional containing id matching {@link ChargingPointType} or an empty optional if no
   *     common charging point type matching the provided id can be found
   */
  public static Optional<ChargingPointType> fromIdString(String id) {
    String cleanedId = StringUtils.cleanString(id).replace("_", "").trim().toLowerCase();
    return Optional.ofNullable(commonChargingPointTypes.get(cleanedId));
  }
}
