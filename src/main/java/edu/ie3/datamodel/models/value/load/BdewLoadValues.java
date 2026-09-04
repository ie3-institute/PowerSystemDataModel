/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value.load;

import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.profile.PowerProfileKey;
import edu.ie3.datamodel.models.value.PValue;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.DoubleStream;

/** Load values for a {@link edu.ie3.datamodel.models.profile.BdewStandardLoadProfile}. */
public abstract sealed class BdewLoadValues implements LoadValues {
  protected final transient Map<String, Double> values = new HashMap<>();

  @Override
  public PValue getValue(ZonedDateTime time, PowerProfileKey powerProfileKey) {
    return BdewStandardLoadProfile.dynamization(
        powerProfileKey, getPower(time), time.getDayOfYear()); // leap years are ignored;
  }

  protected abstract double getPower(ZonedDateTime time);

  /**
   * Method to calculate the maximal value contained in this {@link BdewLoadValues}.
   *
   * @param lastDayOfYear if true, only the values, that could occur at the last day of a year, are
   *     considered
   * @return the maximal value
   */
  public abstract double getMaxValue(boolean lastDayOfYear);

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    return o instanceof BdewLoadValues that;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public String toString() {
    return "BdewLoadValues{" + "}";
  }

  public static final class Bdew1999 extends BdewLoadValues {
    private final double suSa;

    private final double suSu;

    private final double suWd;

    private final double trSa;

    private final double trSu;

    private final double trWd;

    private final double wiSa;

    private final double wiSu;

    private final double wiWd;

    public Bdew1999(
        double suSa,
        double suSu,
        double suWd,
        double trSa,
        double trSu,
        double trWd,
        double wiSa,
        double wiSu,
        double wiWd) {
      super();
      this.suSa = suSa;
      this.suSu = suSu;
      this.suWd = suWd;
      this.trSa = trSa;
      this.trSu = trSu;
      this.trWd = trWd;
      this.wiSa = wiSa;
      this.wiSu = wiSu;
      this.wiWd = wiWd;
      values.put("suSa", suSa);
      values.put("suSu", suSu);
      values.put("suWd", suWd);
      values.put("trSa", trSa);
      values.put("trSu", trSu);
      values.put("trWd", trWd);
      values.put("wiSa", wiSa);
      values.put("wiSu", wiSu);
      values.put("wiWd", wiWd);
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

    @Override
    protected double getPower(ZonedDateTime time) {
      String key = BdewSeason.getSeason(time).getKey().toLowerCase();
      switch (time.getDayOfWeek()) {
        case SATURDAY -> key += "Sa";
        case SUNDAY -> key += "Su";
        default -> key += "Wd";
      }

      return values.get(key);
    }

    @Override
    public double getMaxValue(boolean lastDayOfYear) {
      DoubleStream stream;

      if (lastDayOfYear) {
        stream = DoubleStream.of(wiSa, wiSu, wiWd);
      } else {
        stream = values.values().stream().mapToDouble(d -> d);
      }

      return stream.max().orElse(0.0);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Bdew1999 that)) return false;
      if (!super.equals(o)) return false;
      return suSa == that.suSa
          && suSu == that.suSu
          && suWd == that.suWd
          && trSa == that.trSa
          && trSu == that.trSu
          && trWd == that.trWd
          && wiSa == that.wiSa
          && wiSu == that.wiSu
          && wiWd == that.wiWd;
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), suSa, suSu, suWd, trSa, trSu, trWd, wiSa, wiSu, wiWd);
    }

    @Override
    public String toString() {
      return "Bdew1999{"
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
          + "}";
    }
  }

  public static final class Bdew2025 extends BdewLoadValues {
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

    public Bdew2025(
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
      super();
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
      values.put("janSa", janSa);
      values.put("janSu", janSu);
      values.put("janWd", janWd);
      values.put("febSa", febSa);
      values.put("febSu", febSu);
      values.put("febWd", febWd);
      values.put("marSa", marSa);
      values.put("marSu", marSu);
      values.put("marWd", marWd);
      values.put("aprSa", aprSa);
      values.put("aprSu", aprSu);
      values.put("aprWd", aprWd);
      values.put("maySa", maySa);
      values.put("maySu", maySu);
      values.put("mayWd", mayWd);
      values.put("junSa", junSa);
      values.put("junSu", junSu);
      values.put("junWd", junWd);
      values.put("julSa", julSa);
      values.put("julSu", julSu);
      values.put("julWd", julWd);
      values.put("augSa", augSa);
      values.put("augSu", augSu);
      values.put("augWd", augWd);
      values.put("sepSa", sepSa);
      values.put("sepSu", sepSu);
      values.put("sepWd", sepWd);
      values.put("octSa", octSa);
      values.put("octSu", octSu);
      values.put("octWd", octWd);
      values.put("novSa", novSa);
      values.put("novSu", novSu);
      values.put("novWd", novWd);
      values.put("decSa", decSa);
      values.put("decSu", decSu);
      values.put("decWd", decWd);
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

    @Override
    protected double getPower(ZonedDateTime time) {
      String key = time.getMonth().toString().substring(0, 2).toLowerCase();
      switch (time.getDayOfWeek()) {
        case SATURDAY -> key += "Sa";
        case SUNDAY -> key += "Su";
        default -> key += "Wd";
      }

      return values.get(key);
    }

    @Override
    public double getMaxValue(boolean lastDayOfYear) {
      DoubleStream stream;

      if (lastDayOfYear) {
        stream = DoubleStream.of(decSa, decSu, decWd);
      } else {
        stream = values.values().stream().mapToDouble(d -> d);
      }

      return stream.max().orElse(0.0);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Bdew2025 that)) return false;
      if (!super.equals(o)) return false;
      return janSa == that.janSa
          && janSu == that.janSu
          && janWd == that.janWd
          && febSa == that.febSa
          && febSu == that.febSu
          && febWd == that.febWd
          && marSa == that.marSa
          && marSu == that.marSu
          && marWd == that.marWd
          && aprSa == that.aprSa
          && aprSu == that.aprSu
          && aprWd == that.aprWd
          && maySa == that.maySa
          && maySu == that.maySu
          && mayWd == that.mayWd
          && junSa == that.junSa
          && junSu == that.junSu
          && junWd == that.junWd
          && julSa == that.julSa
          && julSu == that.julSu
          && julWd == that.julWd
          && augSa == that.augSa
          && augSu == that.augSu
          && augWd == that.augWd
          && sepSa == that.sepSa
          && sepSu == that.sepSu
          && sepWd == that.sepWd
          && octSa == that.octSa
          && octSu == that.octSu
          && octWd == that.octWd
          && novSa == that.novSa
          && novSu == that.novSu
          && novWd == that.novWd
          && decSa == that.decSa
          && decSu == that.decSu
          && decWd == that.decWd;
    }

    @Override
    public int hashCode() {
      return Objects.hash(
          super.hashCode(),
          janSa,
          janSu,
          janWd,
          febSa,
          febSu,
          febWd,
          marSa,
          marSu,
          marWd,
          aprSa,
          aprSu,
          aprWd,
          maySa,
          maySu,
          mayWd,
          junSa,
          junSu,
          junWd,
          julSa,
          julSu,
          julWd,
          augSa,
          augSu,
          augWd,
          sepSa,
          sepSu,
          sepWd,
          octSa,
          octSu,
          octWd,
          novSa,
          novSu,
          novWd,
          decSa,
          decSu,
          decWd);
    }

    @Override
    public String toString() {
      return "Bdew2025{"
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
          + "}";
    }
  }
}
