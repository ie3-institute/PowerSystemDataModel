/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value.load;

import static edu.ie3.datamodel.models.BdewSeason.*;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.time.Month.*;
import static java.util.Map.entry;
import static tech.units.indriya.unit.Units.WATT;

import edu.ie3.datamodel.models.BdewSeason;
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.value.PValue;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import tech.units.indriya.quantity.Quantities;

/** Load values for a {@link BdewStandardLoadProfile} */
public sealed interface BdewLoadValues extends LoadValues<BdewStandardLoadProfile>
    permits BdewLoadValues.BDEW1999, BdewLoadValues.BDEW2025 {

  /**
   * Returns the actual power for the given time.
   *
   * @param time given time.
   * @return the power in kW as double
   */
  double getPower(ZonedDateTime time);

  /** Returns the values, that may contain the last day of the year, as a stream. */
  Stream<Double> lastDayOfYearValues();

  /** Returns the values as a stream. */
  Stream<Double> values();

  @Override
  default PValue getValue(ZonedDateTime time, BdewStandardLoadProfile loadProfile) {
    double power =
        switch (loadProfile) {
          case H0, H25, P25, S25 ->
          /* For the residential average profile, a dynamization has to be taken into account */
          dynamization(getPower(time), time.getDayOfYear()); // leap years are ignored
          default -> getPower(time);
        };

    return new PValue(Quantities.getQuantity(power, WATT));
  }

  /**
   * Calculates the dynamization factor for given day of year. Cf. <a
   * href="https://www.bdew.de/media/documents/2000131_Anwendung-repraesentativen_Lastprofile-Step-by-step.pdf">
   * Anwendung der repräsentativen Lastprofile - Step by step</a> page 19
   *
   * @param load load value
   * @param t day of year (1-366)
   * @return dynamization factor
   */
  static double dynamization(double load, int t) {
    double factor =
        (-3.92e-10 * pow(t, 4) + 3.2e-7 * pow(t, 3) - 7.02e-5 * pow(t, 2) + 2.1e-3 * t + 1.24);
    double rndFactor = round(factor * 1e4) / 1e4; // round to 4 decimal places
    return round(load * rndFactor * 1e1) / 1e1; // rounded to 1 decimal place
  }

  /** Scheme for old profiles from the year 1999. */
  final class BDEW1999 implements BdewLoadValues {
    private final double suSa;
    private final double suSu;
    private final double suWd;
    private final double trSa;
    private final double trSu;
    private final double trWd;
    private final double wiSa;
    private final double wiSu;
    private final double wiWd;

    public BDEW1999(
        double suSa,
        double suSu,
        double suWd,
        double trSa,
        double trSu,
        double trWd,
        double wiSa,
        double wiSu,
        double wiWd) {
      this.suSa = suSa;
      this.suSu = suSu;
      this.suWd = suWd;
      this.trSa = trSa;
      this.trSu = trSu;
      this.trWd = trWd;
      this.wiSa = wiSa;
      this.wiSu = wiSu;
      this.wiWd = wiWd;
    }

    @Override
    public double getPower(ZonedDateTime time) {
      Map<BdewSeason, Double> mapping =
          switch (time.getDayOfWeek()) {
            case SATURDAY -> Map.of(
                SUMMER, suSa,
                WINTER, wiSa,
                TRANSITION, trSa);
            case SUNDAY -> Map.of(
                SUMMER, suSu,
                WINTER, wiSu,
                TRANSITION, trSu);
            default -> Map.of(
                SUMMER, suWd,
                WINTER, wiWd,
                TRANSITION, trWd);
          };

      return mapping.get(getSeason(time));
    }

    public double getSuSa() {
      return suSa;
    }

    public double getSuSu() {
      return suSu;
    }

    public double getSuWd() {
      return suWd;
    }

    public double getTrSa() {
      return trSa;
    }

    public double getTrSu() {
      return trSu;
    }

    public double getTrWd() {
      return trWd;
    }

    public double getWiSa() {
      return wiSa;
    }

    public double getWiSu() {
      return wiSu;
    }

    public double getWiWd() {
      return wiWd;
    }

    public Stream<Double> lastDayOfYearValues() {
      return Stream.of(wiSa, wiSu, wiWd);
    }

    public Stream<Double> values() {
      return Stream.of(suSa, suSu, suWd, trSa, trSu, trWd, wiSa, wiSu, wiWd);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      BDEW1999 that = (BDEW1999) o;
      return Objects.equals(suSa, that.suSa)
          && Objects.equals(suSu, that.suSu)
          && Objects.equals(suWd, that.suWd)
          && Objects.equals(trSa, that.trSa)
          && Objects.equals(trSu, that.trSu)
          && Objects.equals(trWd, that.trWd)
          && Objects.equals(wiSa, that.wiSa)
          && Objects.equals(wiSu, that.wiSu)
          && Objects.equals(wiWd, that.wiWd);
    }

    @Override
    public int hashCode() {
      return Objects.hash(suSa, suSu, suWd, trSa, trSu, trWd, wiSa, wiSu, wiWd);
    }

    @Override
    public String toString() {
      return "BDEW1999{"
          + "suSa="
          + suSa
          + ", suSu="
          + suSu
          + ", suWd="
          + suWd
          + ", trSa="
          + trSa
          + ", trSu="
          + trSu
          + ", trWd="
          + trWd
          + ", wiSa="
          + wiSa
          + ", wiSu="
          + wiSu
          + ", wiWd="
          + wiWd
          + '}';
    }
  }

  /** Scheme for updated profiles from the year 2025. */
  final class BDEW2025 implements BdewLoadValues {
    private final double janSa;
    private final double janSu;
    private final double janWd;
    private final double febSa;
    private final double febSu;
    private final double febWd;
    private final double marSa;
    private final double marSu;
    private final double marWd;
    private final double aprSa;
    private final double aprSu;
    private final double aprWd;
    private final double maySa;
    private final double maySu;
    private final double mayWd;
    private final double junSa;
    private final double junSu;
    private final double junWd;
    private final double julSa;
    private final double julSu;
    private final double julWd;
    private final double augSa;
    private final double augSu;
    private final double augWd;
    private final double sepSa;
    private final double sepSu;
    private final double sepWd;
    private final double octSa;
    private final double octSu;
    private final double octWd;
    private final double novSa;
    private final double novSu;
    private final double novWd;
    private final double decSa;
    private final double decSu;
    private final double decWd;

    public BDEW2025(
        double janSa,
        double janSu,
        double janWd,
        double febSa,
        double febSu,
        double febWd,
        double marSa,
        double marSu,
        double marWd,
        double aprSa,
        double aprSu,
        double aprWd,
        double maySa,
        double maySu,
        double mayWd,
        double junSa,
        double junSu,
        double junWd,
        double julSa,
        double julSu,
        double julWd,
        double augSa,
        double augSu,
        double augWd,
        double sepSa,
        double sepSu,
        double sepWd,
        double octSa,
        double octSu,
        double octWd,
        double novSa,
        double novSu,
        double novWd,
        double decSa,
        double decSu,
        double decWd) {
      this.janSa = janSa;
      this.janSu = janSu;
      this.janWd = janWd;
      this.febSa = febSa;
      this.febSu = febSu;
      this.febWd = febWd;
      this.marSa = marSa;
      this.marSu = marSu;
      this.marWd = marWd;
      this.aprSa = aprSa;
      this.aprSu = aprSu;
      this.aprWd = aprWd;
      this.maySa = maySa;
      this.maySu = maySu;
      this.mayWd = mayWd;
      this.junSa = junSa;
      this.junSu = junSu;
      this.junWd = junWd;
      this.julSa = julSa;
      this.julSu = julSu;
      this.julWd = julWd;
      this.augSa = augSa;
      this.augSu = augSu;
      this.augWd = augWd;
      this.sepSa = sepSa;
      this.sepSu = sepSu;
      this.sepWd = sepWd;
      this.octSa = octSa;
      this.octSu = octSu;
      this.octWd = octWd;
      this.novSa = novSa;
      this.novSu = novSu;
      this.novWd = novWd;
      this.decSa = decSa;
      this.decSu = decSu;
      this.decWd = decWd;
    }

    @Override
    public double getPower(ZonedDateTime time) {
      Map<Month, Double> mapping =
          switch (time.getDayOfWeek()) {
            case SATURDAY -> Map.ofEntries(
                entry(JANUARY, janSa),
                entry(FEBRUARY, febSa),
                entry(MARCH, marSa),
                entry(APRIL, aprSa),
                entry(MAY, maySa),
                entry(JUNE, junSa),
                entry(JULY, julSa),
                entry(AUGUST, augSa),
                entry(SEPTEMBER, sepSa),
                entry(OCTOBER, octSa),
                entry(NOVEMBER, novSa),
                entry(DECEMBER, decSa));
            case SUNDAY -> Map.ofEntries(
                entry(JANUARY, janSu),
                entry(FEBRUARY, febSu),
                entry(MARCH, marSu),
                entry(APRIL, aprSu),
                entry(MAY, maySu),
                entry(JUNE, junSu),
                entry(JULY, julSu),
                entry(AUGUST, augSu),
                entry(SEPTEMBER, sepSu),
                entry(OCTOBER, octSu),
                entry(NOVEMBER, novSu),
                entry(DECEMBER, decSu));
            default -> Map.ofEntries(
                entry(JANUARY, janWd),
                entry(FEBRUARY, febWd),
                entry(MARCH, marWd),
                entry(APRIL, aprWd),
                entry(MAY, mayWd),
                entry(JUNE, junWd),
                entry(JULY, julWd),
                entry(AUGUST, augWd),
                entry(SEPTEMBER, sepWd),
                entry(OCTOBER, octWd),
                entry(NOVEMBER, novWd),
                entry(DECEMBER, decWd));
          };

      return mapping.get(time.getMonth());
    }

    public double getJanSa() {
      return janSa;
    }

    public double getJanSu() {
      return janSu;
    }

    public double getJanWd() {
      return janWd;
    }

    public double getFebSa() {
      return febSa;
    }

    public double getFebSu() {
      return febSu;
    }

    public double getFebWd() {
      return febWd;
    }

    public double getMarSa() {
      return marSa;
    }

    public double getMarSu() {
      return marSu;
    }

    public double getMarWd() {
      return marWd;
    }

    public double getAprSa() {
      return aprSa;
    }

    public double getAprSu() {
      return aprSu;
    }

    public double getAprWd() {
      return aprWd;
    }

    public double getMaySa() {
      return maySa;
    }

    public double getMaySu() {
      return maySu;
    }

    public double getMayWd() {
      return mayWd;
    }

    public double getJunSa() {
      return junSa;
    }

    public double getJunSu() {
      return junSu;
    }

    public double getJunWd() {
      return junWd;
    }

    public double getJulSa() {
      return julSa;
    }

    public double getJulSu() {
      return julSu;
    }

    public double getJulWd() {
      return julWd;
    }

    public double getAugSa() {
      return augSa;
    }

    public double getAugSu() {
      return augSu;
    }

    public double getAugWd() {
      return augWd;
    }

    public double getSepSa() {
      return sepSa;
    }

    public double getSepSu() {
      return sepSu;
    }

    public double getSepWd() {
      return sepWd;
    }

    public double getOctSa() {
      return octSa;
    }

    public double getOctSu() {
      return octSu;
    }

    public double getOctWd() {
      return octWd;
    }

    public double getNovSa() {
      return novSa;
    }

    public double getNovSu() {
      return novSu;
    }

    public double getNovWd() {
      return novWd;
    }

    public double getDecSa() {
      return decSa;
    }

    public double getDecSu() {
      return decSu;
    }

    public double getDecWd() {
      return decWd;
    }

    public Stream<Double> lastDayOfYearValues() {
      return Stream.of(decSa, decSa, decWd);
    }

    public Stream<Double> values() {
      return Stream.of(
          janSa, janSu, janWd, febSa, febSu, febWd, marSa, marSu, marWd, aprSa, aprSu, aprWd, maySa,
          maySu, mayWd, junSa, junSu, junWd, julSa, julSu, julWd, augSa, augSu, augWd, sepSa, sepSu,
          sepWd, octSa, octSu, octWd, novSa, novSu, novWd, decSa, decSu, decWd);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      BDEW2025 that = (BDEW2025) o;
      return Objects.equals(janSa, that.janSa)
          && Objects.equals(janSu, that.janSu)
          && Objects.equals(janWd, that.janWd)
          && Objects.equals(febSa, that.febSa)
          && Objects.equals(febSu, that.febSu)
          && Objects.equals(febWd, that.febWd)
          && Objects.equals(marSa, that.marSa)
          && Objects.equals(marSu, that.marSu)
          && Objects.equals(marWd, that.marWd)
          && Objects.equals(aprSa, that.aprSa)
          && Objects.equals(aprSu, that.aprSu)
          && Objects.equals(aprWd, that.aprWd)
          && Objects.equals(maySa, that.maySa)
          && Objects.equals(maySu, that.maySu)
          && Objects.equals(mayWd, that.mayWd)
          && Objects.equals(junSa, that.junSa)
          && Objects.equals(junSu, that.junSu)
          && Objects.equals(junWd, that.junWd)
          && Objects.equals(julSa, that.julSa)
          && Objects.equals(julSu, that.julSu)
          && Objects.equals(julWd, that.julWd)
          && Objects.equals(augSa, that.augSa)
          && Objects.equals(augSu, that.augSu)
          && Objects.equals(augWd, that.augWd)
          && Objects.equals(sepSa, that.sepSa)
          && Objects.equals(sepSu, that.sepSu)
          && Objects.equals(sepWd, that.sepWd)
          && Objects.equals(octSa, that.octSa)
          && Objects.equals(octSu, that.octSu)
          && Objects.equals(octWd, that.octWd)
          && Objects.equals(novSa, that.novSa)
          && Objects.equals(novSu, that.novSu)
          && Objects.equals(novWd, that.novWd)
          && Objects.equals(decSa, that.decSa)
          && Objects.equals(decSu, that.decSu)
          && Objects.equals(decWd, that.decWd);
    }

    @Override
    public int hashCode() {
      return Objects.hash(
          janSa, janSu, janWd, febSa, febSu, febWd, marSa, marSu, marWd, aprSa, aprSu, aprWd, maySa,
          maySu, mayWd, junSa, junSu, junWd, julSa, julSu, julWd, augSa, augSu, augWd, sepSa, sepSu,
          sepWd, octSa, octSu, octWd, novSa, novSu, novWd, decSa, decSu, decWd);
    }

    @Override
    public String toString() {
      return "BDEW1999{"
          + "janSa="
          + janSa
          + ", janSu="
          + janSu
          + ", janWd="
          + janWd
          + ", febSa="
          + febSa
          + ", febSu="
          + febSu
          + ", febWd="
          + febWd
          + ", marSa="
          + marSa
          + ", marSu="
          + marSu
          + ", marWd="
          + marWd
          + ", aprSa="
          + aprSa
          + ", aprSu="
          + aprSu
          + ", aprWd="
          + aprWd
          + ", maySa="
          + maySa
          + ", maySu="
          + maySu
          + ", mayWd="
          + mayWd
          + ", junSa="
          + junSa
          + ", junSu="
          + junSu
          + ", junWd="
          + junWd
          + ", julSa="
          + julSa
          + ", julSu="
          + julSu
          + ", julWd="
          + julWd
          + ", augSa="
          + augSa
          + ", augSu="
          + augSu
          + ", augWd="
          + augWd
          + ", sepSa="
          + sepSa
          + ", sepSu="
          + sepSu
          + ", sepWd="
          + sepWd
          + ", octSa="
          + octSa
          + ", octSu="
          + octSu
          + ", octWd="
          + octWd
          + ", novSa="
          + novSa
          + ", novSu="
          + novSu
          + ", novWd="
          + novWd
          + ", decSa="
          + decSa
          + ", decSu="
          + decSu
          + ", decWd="
          + decWd
          + '}';
    }
  }
}
