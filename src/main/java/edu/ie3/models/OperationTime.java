package edu.ie3.models;

import edu.ie3.util.interval.ClosedInterval;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

public class OperationTime {

    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private boolean isLimited;

    public OperationTime() {}

    protected OperationTime(ZonedDateTime startDate, ZonedDateTime endDate, boolean isLimited) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isLimited = isLimited;
    }

    public Optional<ZonedDateTime> getStartDate() {
        return Optional.ofNullable(startDate);
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public Optional<ZonedDateTime> getEndDate() {
        return Optional.ofNullable(endDate);
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isLimited() {
        return isLimited;
    }

    public void setLimited(boolean limited) {
        isLimited = limited;
    }

    public Optional<ClosedInterval<ZonedDateTime>> getOperationTime() {
        if(!isLimited) return Optional.empty();
        ZonedDateTime adjustedStartDate = getStartDate().orElse(ZonedDateTime.of(LocalDateTime.MIN, ZoneId.of("UTC")));
        ZonedDateTime adjustedEndDate = getEndDate().orElse(ZonedDateTime.of(LocalDateTime.MAX, ZoneId.of("UTC")));
        return Optional.of(new ClosedInterval<>(adjustedStartDate, adjustedEndDate));
    }

    public boolean includes(ZonedDateTime date){
        Optional<ClosedInterval<ZonedDateTime>> optOperationTime= getOperationTime();
        return optOperationTime.isEmpty() || optOperationTime.get().includes(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationTime that = (OperationTime) o;
        if(isLimited != that.isLimited) return false;
        if(!isLimited) return true; // when the OperationTimes are not limited, the dates are not needed
        return Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate, isLimited);
    }

    public static OperationTimeBuilder builder() {
        return new OperationTimeBuilder();
    }

    public static class OperationTimeBuilder {

        private ZonedDateTime startDate;
        private ZonedDateTime endDate;
        private boolean isLimited;

        public OperationTimeBuilder withStart(ZonedDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public OperationTimeBuilder withEnd(ZonedDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public OperationTimeBuilder withTimeLimitation() {
            this.isLimited = true;
            return this;
        }

        public OperationTimeBuilder withoutTimeLimitation() {
            this.isLimited = false;
            return this;
        }

        public OperationTimeBuilder withOperationInterval(ClosedInterval<ZonedDateTime> timeInterval) {
            this.startDate = timeInterval.getLower();
            this.endDate = timeInterval.getUpper();
            return this;
        }

        public OperationTime build() {
            return new OperationTime(startDate, endDate, isLimited);
        }
    }
}
