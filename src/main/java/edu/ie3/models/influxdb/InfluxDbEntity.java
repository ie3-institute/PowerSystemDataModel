package edu.ie3.models.influxdb;

import edu.ie3.models.UniqueEntity;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.TimeColumn;

import java.time.Instant;

public abstract class InfluxDbEntity<E extends UniqueEntity> {

    @TimeColumn
    @Column(name = "time")
    protected Instant time;

    public InfluxDbEntity(Instant time) {
        this.time = time;
    }

    public InfluxDbEntity() {}

}