package edu.ie3.models.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.vividsolutions.jts.geom.Point;
import edu.ie3.dataconnection.source.CsvCoordinateSource;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.WeatherValues;
import edu.ie3.util.quantities.interfaces.Irradiation;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CsvWeatherInput {
    @CsvBindByName
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    LocalDateTime datum;
    @CsvBindByName
    Integer koordinatenid;
    @CsvBindByName
    Double diffusstrahlung;
    @CsvBindByName
    Double direktstrahlung;
    @CsvBindByName
    Double temperatur;
    @CsvBindByName
    Double windrichtung;
    @CsvBindByName
    Double windgeschwindigkeit;

    public CsvWeatherInput() {}

    public CsvWeatherInput(
            LocalDateTime datum,
            Integer koordinatenid,
            Double diffusstrahlung,
            Double direkstrahlung,
            Double temperatur,
            Double windrichtung,
            Double windgeschwindigkeit) {
        this.datum = datum;
        this.koordinatenid = koordinatenid;
        this.diffusstrahlung = diffusstrahlung;
        this.direktstrahlung = direkstrahlung;
        this.temperatur = temperatur;
        this.windrichtung = windrichtung;
        this.windgeschwindigkeit = windgeschwindigkeit;
    }

    public TimeBasedValue<WeatherValues> toTimeBasedWeatherValues() {
        Point geometry = CsvCoordinateSource.getCoordinate(koordinatenid);
        Quantity<Irradiation> diffuseIrradiation =
                Quantities.getQuantity(diffusstrahlung, StandardUnits.IRRADIATION);
        Quantity<Irradiation> directIrradiation =
                Quantities.getQuantity(direktstrahlung, StandardUnits.IRRADIATION);
        Quantity<Temperature> temperature = Quantities.getQuantity(temperatur, StandardUnits.TEMPERATURE);
        Quantity<Angle> direction = Quantities.getQuantity(windrichtung, StandardUnits.WIND_DIRECTION);
        Quantity<Speed> velocity =
                Quantities.getQuantity(windgeschwindigkeit, StandardUnits.WIND_VELOCITY);
        return new TimeBasedValue<>(
                ZonedDateTime.of(datum, ZoneId.of("UTC")),
                new WeatherValues(
                        geometry, diffuseIrradiation, directIrradiation, temperature, direction, velocity));
    }

    public LocalDateTime getDatum() {
        return datum;
    }

    public void setDatum(LocalDateTime datum) {
        this.datum = datum;
    }

    public Integer getKoordinatenid() {
        return koordinatenid;
    }

    public void setKoordinatenid(Integer koordinatenid) {
        this.koordinatenid = koordinatenid;
    }

    public Double getDiffusstrahlung() {
        return diffusstrahlung;
    }

    public void setDiffusstrahlung(Double diffusstrahlung) {
        this.diffusstrahlung = diffusstrahlung;
    }

    public Double getDirektstrahlung() {
        return direktstrahlung;
    }

    public void setDirektstrahlung(Double direktstrahlung) {
        this.direktstrahlung = direktstrahlung;
    }

    public Double getTemperatur() {
        return temperatur;
    }

    public void setTemperatur(Double temperatur) {
        this.temperatur = temperatur;
    }

    public Double getWindrichtung() {
        return windrichtung;
    }

    public void setWindrichtung(Double windrichtung) {
        this.windrichtung = windrichtung;
    }

    public Double getWindgeschwindigkeit() {
        return windgeschwindigkeit;
    }

    public void setWindgeschwindigkeit(Double windgeschwindigkeit) {
        this.windgeschwindigkeit = windgeschwindigkeit;
    }
}