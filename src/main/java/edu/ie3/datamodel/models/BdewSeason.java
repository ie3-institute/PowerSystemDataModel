/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

import edu.ie3.datamodel.exceptions.ParsingException;
import java.time.ZonedDateTime;

public enum BdewSeason {
  WINTER("Wi"),
  SUMMER("Su"),
  TRANSITION("Tr");

  private final String key;

  BdewSeason(String key) {
    this.key = key.toLowerCase();
  }

  public static BdewSeason parse(String key) throws ParsingException {
    return switch (key) {
      case "Wi", "Winter" -> WINTER;
      case "Su", "Summer" -> SUMMER;
      case "Tr", "Intermediate" -> TRANSITION;
      default -> throw new ParsingException(
          "There is no season for key:"
              + key
              + ". Permissible keys: 'Wi', 'Winter', 'Su', 'Summer', 'Tr', 'Intermediate'");
    };
  }

  /**
   * Creates a season from given time
   *
   * @param time the time
   * @return a season
   */
  public static BdewSeason getSeason(ZonedDateTime time) {
    int day = time.getDayOfMonth();

    // winter:      1.11.-20.03.
    // summer:     15.05.-14.09.
    // transition: 21.03.-14.05. and
    //             15.09.-31.10.
    // (VDEW handbook)

    return switch (time.getMonth()) {
      case NOVEMBER, DECEMBER, JANUARY, FEBRUARY -> WINTER;
      case MARCH -> {
        if (day <= 20) {
          yield WINTER;
        } else {
          yield TRANSITION;
        }
      }
      case MAY -> {
        if (day >= 15) {
          yield SUMMER;
        } else {
          yield TRANSITION;
        }
      }
      case JUNE, JULY, AUGUST -> SUMMER;
      case SEPTEMBER -> {
        if (day <= 14) {
          yield SUMMER;
        } else {
          yield TRANSITION;
        }
      }
      default -> TRANSITION;
    };
  }

  public String getKey() {
    return key;
  }

  @Override
  public String toString() {
    return key;
  }
}
