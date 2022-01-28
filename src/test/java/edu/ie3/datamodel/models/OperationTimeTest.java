/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

import static org.junit.jupiter.api.Assertions.*;

import edu.ie3.util.interval.ClosedInterval;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class OperationTimeTest {

  private static final ZonedDateTime START_DATE =
      ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
  private static final ZonedDateTime END_DATE =
      ZonedDateTime.of(2020, 12, 31, 23, 59, 0, 0, ZoneId.of("UTC"));
  private static final ZonedDateTime DATE_IN_INTERVAL =
      ZonedDateTime.of(2020, 7, 3, 3, 40, 0, 0, ZoneId.of("UTC"));
  private static final ZonedDateTime DATE_BEFORE_INTERVAL =
      ZonedDateTime.of(2019, 3, 17, 0, 0, 0, 0, ZoneId.of("UTC"));
  private static final ZonedDateTime DATE_AFTER_INTERVAL =
      ZonedDateTime.of(2022, 3, 17, 0, 0, 0, 0, ZoneId.of("UTC"));

  private static final ZonedDateTime MIN_DATE =
      ZonedDateTime.of(LocalDateTime.MIN, ZoneId.of("UTC"));
  private static final ZonedDateTime MAX_DATE =
      ZonedDateTime.of(LocalDateTime.MAX, ZoneId.of("UTC"));

  private static final OperationTime LIMITED_OPERATION_TIME =
      new OperationTime(START_DATE, END_DATE, true);
  private static final OperationTime LIMITED_OPERATION_TIME_START_ONLY =
      new OperationTime(START_DATE, null, true);
  private static final OperationTime LIMITED_OPERATION_TIME_END_ONLY =
      new OperationTime(null, END_DATE, true);
  private static final OperationTime NOT_LIMITED_OPERATION = new OperationTime(null, null, false);

  @Test
  void notLimited() {
    OperationTime notLimited = OperationTime.notLimited();
    assertEquals(NOT_LIMITED_OPERATION, notLimited);
    assertFalse(notLimited.isLimited());
    assertFalse(notLimited.getStartDate().isPresent());
    assertFalse(notLimited.getEndDate().isPresent());
    assertEquals(Optional.empty(), notLimited.getOperationLimit());
  }

  @Test
  void getStartDate() {
    Optional<ZonedDateTime> optStartDate = LIMITED_OPERATION_TIME.getStartDate();
    assertTrue(optStartDate.isPresent());
    ZonedDateTime startDate = optStartDate.get();
    assertEquals(START_DATE, startDate);

    optStartDate = LIMITED_OPERATION_TIME_START_ONLY.getStartDate();
    assertTrue(optStartDate.isPresent());
    startDate = optStartDate.get();
    assertEquals(START_DATE, startDate);

    optStartDate = LIMITED_OPERATION_TIME_END_ONLY.getStartDate();
    assertFalse(optStartDate.isPresent());

    optStartDate = NOT_LIMITED_OPERATION.getStartDate();
    assertFalse(optStartDate.isPresent());
  }

  @Test
  void getEndDate() {
    Optional<ZonedDateTime> optEndDate = LIMITED_OPERATION_TIME.getEndDate();
    assertTrue(optEndDate.isPresent());
    ZonedDateTime endDate = optEndDate.get();
    assertEquals(END_DATE, endDate);

    optEndDate = LIMITED_OPERATION_TIME_START_ONLY.getEndDate();
    assertFalse(optEndDate.isPresent());

    optEndDate = LIMITED_OPERATION_TIME_END_ONLY.getEndDate();
    assertTrue(optEndDate.isPresent());
    endDate = optEndDate.get();
    assertEquals(END_DATE, endDate);

    optEndDate = NOT_LIMITED_OPERATION.getEndDate();
    assertFalse(optEndDate.isPresent());
  }

  @Test
  void getOperationLimit() {
    Optional<ClosedInterval<ZonedDateTime>> optOperationLimit =
        LIMITED_OPERATION_TIME.getOperationLimit();
    assertTrue(optOperationLimit.isPresent());
    ClosedInterval<ZonedDateTime> operationLimit = optOperationLimit.get();
    assertEquals(new ClosedInterval<>(START_DATE, END_DATE), operationLimit);

    optOperationLimit = LIMITED_OPERATION_TIME_START_ONLY.getOperationLimit();
    assertTrue(optOperationLimit.isPresent());
    operationLimit = optOperationLimit.get();
    assertEquals(new ClosedInterval<>(START_DATE, MAX_DATE), operationLimit);

    optOperationLimit = LIMITED_OPERATION_TIME_END_ONLY.getOperationLimit();
    assertTrue(optOperationLimit.isPresent());
    operationLimit = optOperationLimit.get();
    assertEquals(new ClosedInterval<>(MIN_DATE, END_DATE), operationLimit);

    optOperationLimit = NOT_LIMITED_OPERATION.getOperationLimit();
    assertFalse(optOperationLimit.isPresent());
  }

  @Test
  void includes() {
    assertTrue(LIMITED_OPERATION_TIME.includes(DATE_IN_INTERVAL));
    assertFalse(LIMITED_OPERATION_TIME.includes(DATE_BEFORE_INTERVAL));
    assertFalse(LIMITED_OPERATION_TIME.includes(DATE_AFTER_INTERVAL));

    assertTrue(LIMITED_OPERATION_TIME_START_ONLY.includes(DATE_IN_INTERVAL));
    assertFalse(LIMITED_OPERATION_TIME_START_ONLY.includes(DATE_BEFORE_INTERVAL));
    assertTrue(LIMITED_OPERATION_TIME_START_ONLY.includes(DATE_AFTER_INTERVAL));

    assertTrue(LIMITED_OPERATION_TIME_END_ONLY.includes(DATE_IN_INTERVAL));
    assertTrue(LIMITED_OPERATION_TIME_END_ONLY.includes(DATE_BEFORE_INTERVAL));
    assertFalse(LIMITED_OPERATION_TIME_END_ONLY.includes(DATE_AFTER_INTERVAL));

    assertTrue(NOT_LIMITED_OPERATION.includes(DATE_IN_INTERVAL));
    assertTrue(NOT_LIMITED_OPERATION.includes(DATE_BEFORE_INTERVAL));
    assertTrue(NOT_LIMITED_OPERATION.includes(DATE_AFTER_INTERVAL));
  }
}
