
package edu.ie3.models;

import edu.ie3.util.interval.ClosedInterval;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OperationTimeTest {

    private static final ZonedDateTime START_DATE = ZonedDateTime.of(2020, 1, 1, 0,
            0, 0, 0, ZoneId.of("UTC"));
    private static final ZonedDateTime END_DATE = ZonedDateTime.of(2020, 12, 31, 23,
            59, 0, 0, ZoneId.of("UTC"));
    private static final ZonedDateTime DATE_IN_INTERVAL = ZonedDateTime.of(2020, 7, 3, 3,
            40, 0, 0, ZoneId.of("UTC"));
    private static final ZonedDateTime DATE_BEFORE_INTERVAL = ZonedDateTime.of(2019, 3, 17, 0,
            0, 0, 0, ZoneId.of("UTC"));
    private static final ZonedDateTime DATE_AFTER_INTERVAL = ZonedDateTime.of(2022, 3, 17, 0,
            0, 0, 0, ZoneId.of("UTC"));

    private static final ZonedDateTime MIN_DATE = ZonedDateTime.of(LocalDateTime.MIN, ZoneId.of("UTC"));
    private static final ZonedDateTime MAX_DATE = ZonedDateTime.of(LocalDateTime.MAX, ZoneId.of("UTC"));

    private static final OperationTime LIMITED_OPERATION_TIME = new OperationTime(START_DATE, END_DATE, true);
    private static final OperationTime LIMITED_OPERATION_TIME_START_ONLY = new OperationTime(START_DATE, null, true);
    private static final OperationTime LIMITED_OPERATION_TIME_END_ONLY = new OperationTime(null, END_DATE, true);
    private static final OperationTime NOT_LIMITED_OPERATION = new OperationTime(null, null, false);

    @Test
    void notLimited() {
        OperationTime notLimited = OperationTime.notLimited();
        assertEquals(NOT_LIMITED_OPERATION, notLimited);
        assertFalse(notLimited.isLimited());
        assertTrue(notLimited.getStartDate().isEmpty());
        assertTrue(notLimited.getEndDate().isEmpty());
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
        assertTrue(optStartDate.isEmpty());
        
        optStartDate = NOT_LIMITED_OPERATION.getStartDate();
        assertTrue(optStartDate.isEmpty());
    }

    @Test
    void getEndDate() {
        Optional<ZonedDateTime> optEndDate = LIMITED_OPERATION_TIME.getEndDate();
        assertTrue(optEndDate.isPresent());
        ZonedDateTime endDate = optEndDate.get();
        assertEquals(END_DATE, endDate);

        optEndDate = LIMITED_OPERATION_TIME_START_ONLY.getEndDate();
        assertTrue(optEndDate.isEmpty());

        optEndDate = LIMITED_OPERATION_TIME_END_ONLY.getEndDate();
        assertTrue(optEndDate.isPresent());
        endDate = optEndDate.get();
        assertEquals(END_DATE, endDate);

        optEndDate = NOT_LIMITED_OPERATION.getEndDate();
        assertTrue(optEndDate.isEmpty());
    }

    @Test
    void getOperationLimit() {
        Optional<ClosedInterval<ZonedDateTime>> optOperationLimit = LIMITED_OPERATION_TIME.getOperationLimit();
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
        assertTrue(optOperationLimit.isEmpty());
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



    @Test
    void builder() {
        //Test empty build
        OperationTime.OperationTimeBuilder builder = OperationTime.builder();
        assertEquals(NOT_LIMITED_OPERATION, builder.build());

        //Test adding parameters
        builder = builder.withStart(START_DATE);
        assertEquals(LIMITED_OPERATION_TIME_START_ONLY, builder.build());

        builder = builder.withEnd(END_DATE);
        assertEquals(LIMITED_OPERATION_TIME, builder.build());

        //Test overriding
        builder = builder.withStart(null);
        assertEquals(LIMITED_OPERATION_TIME_END_ONLY, builder.build());

        builder = builder.withEnd(null);
        assertEquals(NOT_LIMITED_OPERATION, builder.build());

        //Test build from Interval
        builder = OperationTime.builder();
        ClosedInterval<ZonedDateTime> timeInterval = new ClosedInterval<>(START_DATE, END_DATE);
        builder = builder.withOperationTime(timeInterval);
        assertEquals(LIMITED_OPERATION_TIME, builder.build());
    }

}