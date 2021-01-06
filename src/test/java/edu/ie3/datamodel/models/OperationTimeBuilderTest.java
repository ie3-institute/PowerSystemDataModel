/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.ie3.util.interval.ClosedInterval;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

class OperationTimeBuilderTest {

  private static final ZonedDateTime START_DATE =
      ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
  private static final ZonedDateTime END_DATE =
      ZonedDateTime.of(2020, 12, 31, 23, 59, 0, 0, ZoneId.of("UTC"));

  private static final OperationTime LIMITED_OPERATION_TIME =
      new OperationTime(START_DATE, END_DATE, true);
  private static final OperationTime LIMITED_OPERATION_TIME_START_ONLY =
      new OperationTime(START_DATE, null, true);
  private static final OperationTime LIMITED_OPERATION_TIME_END_ONLY =
      new OperationTime(null, END_DATE, true);
  private static final OperationTime NOT_LIMITED_OPERATION = new OperationTime(null, null, false);

  @Test
  protected void builder() {
    // Test empty build
    OperationTime.OperationTimeBuilder builder = new OperationTime.OperationTimeBuilder();
    assertEquals(NOT_LIMITED_OPERATION, builder.build());

    // Test adding parameters
    builder = builder.withStart(START_DATE);
    assertEquals(LIMITED_OPERATION_TIME_START_ONLY, builder.build());

    builder = builder.withEnd(END_DATE);
    assertEquals(LIMITED_OPERATION_TIME, builder.build());

    // Test overriding
    builder = builder.withStart(null);
    assertEquals(LIMITED_OPERATION_TIME_END_ONLY, builder.build());

    builder = builder.withEnd(null);
    assertEquals(NOT_LIMITED_OPERATION, builder.build());

    // Test build from Interval
    builder = new OperationTime.OperationTimeBuilder();
    ClosedInterval<ZonedDateTime> timeInterval = new ClosedInterval<>(START_DATE, END_DATE);
    builder = builder.withOperationTime(timeInterval);
    assertEquals(LIMITED_OPERATION_TIME, builder.build());
  }
}
