/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile;

import edu.ie3.datamodel.exceptions.ParsingException;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;

public interface LoadProfileKey {

  static BDEWLoadProfileKey parseBDEWProfile(String key) throws ParsingException {
    String profile = key.substring(0, 2);
    String season = key.substring(2, 4);
    String dayString = key.substring(4, 6);

    DayOfWeek day =
        switch (dayString) {
          case "Sa" -> DayOfWeek.SATURDAY;
          case "Su" -> DayOfWeek.SUNDAY;
          default -> DayOfWeek.MONDAY;
        };

    return new BDEWLoadProfileKey(BdewStandardLoadProfile.get(profile), Season.parse(season), day);
  }

  record BDEWLoadProfileKey(LoadProfile profile, Season season, DayOfWeek dayOfWeek)
      implements LoadProfileKey {}

  enum Season {
    WINTER("Wi"),
    SUMMER("Su"),
    TRANSITION("Tr");

    private final String key;

    Season(String key) {
      this.key = key.toLowerCase();
    }

    public static Season parse(String key) throws ParsingException {
      return switch (key) {
        case "Wi" -> WINTER;
        case "Su" -> SUMMER;
        case "Tr" -> TRANSITION;
        default -> throw new ParsingException("There is no season for key:" + key);
      };
    }

    /**
     * Creates a season from given time
     *
     * @param time the time
     * @return a season
     */
    public static Season get(ZonedDateTime time) {
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
      return "Season{" + "key='" + key + '\'' + '}';
    }
  }
}
