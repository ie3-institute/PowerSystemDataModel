package edu.ie3.datamodel;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.file.FileType;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.FileLoadProfileMetaInformation;
import edu.ie3.datamodel.io.sink.CsvFileSink;
import edu.ie3.datamodel.io.source.PowerValueSource;
import edu.ie3.datamodel.io.source.json.JsonDataSource;
import edu.ie3.datamodel.io.source.json.JsonMarkovProfileSource;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.util.TimeUtil;
import edu.ie3.util.interval.ClosedInterval;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MarkovGenerator {

    private static TimeUtil timeUtil = TimeUtil.withDefaults;

    public static void main(String[] args) throws SourceException, EntityProcessorException {
        Path input = Path.of("input").toAbsolutePath();
        Path output = Path.of("output").toAbsolutePath();

        var baseline = new FileLoadProfileMetaInformation("baseline", input.resolve("psdm_model_baselines.json"), FileType.JSON);
        var additional = new FileLoadProfileMetaInformation("additional", input.resolve("psdm_model_additional.json"), FileType.JSON);


        JsonDataSource source = new JsonDataSource(input, new FileNamingStrategy());

        var markovSource = new JsonMarkovProfileSource(source, baseline);

        var model = markovSource.getModel();

        var t = ZonedDateTime.now();
        var sink = new CsvFileSink(output.resolve("baseline_" + t.getMonthValue() + "-" + t.getDayOfMonth() + " " + t.getHour() + ":" + t.getMinute() + ":" + t.getSecond()));

        ZonedDateTime start = timeUtil.toZonedDateTime("2025-07-01T00:00:00Z");
        ZonedDateTime end = timeUtil.toZonedDateTime("2025-07-02T00:00:00Z");

        var interval = new ClosedInterval<>(start, end);

       run(1000,10, interval, model).forEach(sink::persistTimeSeries);
    }

    private static List<IndividualTimeSeries<PValue>> run(int n, int warmUpHours, ClosedInterval<ZonedDateTime> range, MarkovLoadModel model)  {
        List<IndividualTimeSeries<PValue>> series = new ArrayList<>();

        ZonedDateTime start = range.getLower();
        ZonedDateTime end = range.getUpper();

        for (int i=0;i<n;i++) {
            long seed = ThreadLocalRandom.current().nextLong();

            System.out.println("Count (seed: " + seed + "): " + i);
            SortedSet<TimeBasedValue<PValue>> values = new TreeSet<>(TimeBasedValue::compareTo);

            ZonedDateTime current = start.minusHours(warmUpHours);
            OptionalInt previousState = OptionalInt.of(0);
            OptionalDouble initialNormalizedValue = OptionalDouble.empty();


            while (current.isBefore(start)) {
                PowerValueSource.MarkovIdentifier identifier = new PowerValueSource.MarkovIdentifier(current, previousState, initialNormalizedValue, seed);
                PowerValueSource.MarkovOutputValue value = model.getValueSupplier(identifier).get();

                previousState = OptionalInt.of(value.nextState());

                current = current.plusMinutes(15);
            }


            while (!current.isAfter(end)) {
                PowerValueSource.MarkovIdentifier identifier = new PowerValueSource.MarkovIdentifier(current, previousState, initialNormalizedValue, seed);
                PowerValueSource.MarkovOutputValue value = model.getValueSupplier(identifier).get();
                previousState = OptionalInt.of(value.nextState());

                PValue power = value.value().orElse(null);
                values.add(new TimeBasedValue<>(current, power));

                current = current.plusMinutes(15);
            }

            series.add(new IndividualTimeSeries<>(values));
        }

        System.out.println("Finished.");

        return series;
    }
}
