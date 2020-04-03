/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

import edu.ie3.util.interval.ClosedInterval;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Time for which something is operated, can be limited to a time frame with one or two bounds or
 * not limited which is by definition equal to always on
 */
public class OperationTime {

  /** Date of operation start */
  private ZonedDateTime startDate;
  /** Date of operation end */
  private ZonedDateTime endDate;
  /** Is the operation time frame limited? */
  private boolean isLimited;

  /** Constructor for builder only */
  protected OperationTime(ZonedDateTime startDate, ZonedDateTime endDate, boolean isLimited) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.isLimited = isLimited;
  }

  /** Constructor for OperationTime without limitations (= always one) */
  private OperationTime() {}

  /**
   * @return an OperationTime without time limitations (= always on)
   */
  public static OperationTime notLimited() {
    return new OperationTime();
  }

  /** @return date of operation start, if present */
  public Optional<ZonedDateTime> getStartDate() {
    return Optional.ofNullable(startDate);
  }

  /** @return date of operation end, if present */
  public Optional<ZonedDateTime> getEndDate() {
    return Optional.ofNullable(endDate);
  }

  public boolean isLimited() {
    return isLimited;
  }

  /**
   * @return Optional.empty(), if the time frame is unlimited <br>
   *     <br>
   *     Optional.of(ClosedInterval(startDate, endDate)), if the upper and lower bound is set <br>
   *     <br>
   *     Optional.of((ClosedInterval(startDate, LocalDateTime.MAX)), if only the lower bound is set
   *     <br>
   *     <br>
   *     Optional.of((ClosedInterval(LocalDateTime.MIN, endDate)), if only the upper bound is set
   */
  public Optional<ClosedInterval<ZonedDateTime>> getOperationLimit() {
    if (!isLimited) return Optional.empty();
    ZonedDateTime adjustedStartDate =
        getStartDate().orElse(ZonedDateTime.of(LocalDateTime.MIN, ZoneId.of("UTC")));
    ZonedDateTime adjustedEndDate =
        getEndDate().orElse(ZonedDateTime.of(LocalDateTime.MAX, ZoneId.of("UTC")));
    return Optional.of(new ClosedInterval<>(adjustedStartDate, adjustedEndDate));
  }

  /**
   * Check if given date is included in the operation time frame
   *
   * @return true, if the date is included or there are no operation time limitations, else false
   */
  public boolean includes(ZonedDateTime date) {
    Optional<ClosedInterval<ZonedDateTime>> optOperationTime = getOperationLimit();
    return !optOperationTime.isPresent() || optOperationTime.get().includes(date);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OperationTime that = (OperationTime) o;
    if (isLimited != that.isLimited) return false;
    if (!isLimited)
      return true; // when the OperationTimes are not limited, the dates are not needed
    return Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startDate, endDate, isLimited);
  }

  @Override
  public String toString() {
    return "OperationTime{"
        + "startDate="
        + startDate
        + ", endDate="
        + endDate
        + ", isLimited="
        + isLimited
        + '}';
  }

  /** @return OperationTimeBuilder instance */
  public static OperationTimeBuilder builder() {
    return new OperationTimeBuilder();
  }

  /** Builder class for {@link edu.ie3.datamodel.models.OperationTime} */
  public static class OperationTimeBuilder {

    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    /** Set the date of operation start */
    public OperationTimeBuilder withStart(ZonedDateTime startDate) {
      this.startDate = startDate;
      return this;
    }

    /** Set the date of operation end */
    public OperationTimeBuilder withEnd(ZonedDateTime endDate) {
      this.endDate = endDate;
      return this;
    }

    /** Set dates of operation start and end from interval */
    public OperationTimeBuilder withOperationTime(ClosedInterval<ZonedDateTime> timeInterval) {
      this.startDate = timeInterval.getLower();
      this.endDate = timeInterval.getUpper();
      return this;
    }

    /**
     * Build an {@link edu.ie3.datamodel.models.OperationTime} instance from the given parameters
     * <br>
     * If both time frame bounds, start and end date, are not set, the OperationTime is regarded not
     * limited
     */
    public OperationTime build() {
      boolean isLimited = (startDate != null || endDate != null);
      return new OperationTime(startDate, endDate, isLimited);
    }
  }
}
