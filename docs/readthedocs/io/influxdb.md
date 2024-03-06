# InfluxDB

[InfluxDB](https://www.influxdata.com/products/influxdb-overview/) is a time series database. As such, it can only
handle time based data like weather data or results.
The PowerSystemDataModel offers two interface implementations for InfluxDB 1.x: WeatherSource and OutputDataSink.

## Introduction to InfluxDB

InfluxDB is a NoSQL database as it is neither relational nor able to handle SQL queries, even though InfluxDB's own
QueryLanguage, [InfluxQL](https://docs.influxdata.com/influxdb/v1.8/query_language/) is very similar to SQL.
InfluxDB persists data in *measurements*. A measurement is comparable to a table in a relational data model. It consists
of a *measurement name*, *fields*, *tags* and a *time column*. The measurement name is the equivalent of a table name. Fields
and tags are similar as they both hold data like columns in relational data. But while fields are supposed to hold
the actual data, tags should only hold metadata, which is why tag values can only be strings. Under default
configuration, one tag key can only hold 10 000 distinct tag values. This choice was made as tags are indexed and
supposed to be queried. Fields should only be queried if not avoidable. The time column is automatically provided, it
holds timestamps in [RFC3339 UTC](https://www.ietf.org/rfc/rfc3339.txt), which for example looks like
"2020-06-22T10:14:50.52Z". The equivalent to a table row is a measurement point. It holds field and tag values as well
as the time. While the data values are optional, a timestamp is not. If no time is provided when persisting, the current
system time is used.

## Instantiating an InfluxDB DataConnector

To instantiate an InfluxDbConnector a connection url, a database name and a scenario name should be provided. The
scenario name is used to build measurement names for results.
If none of those are provided, default values are used.

```java
InfluxDbConnector unparameterizedInfluxDb = new InfluxDbConnector();
InfluxDbConnector defaultInfluxDb = new InfluxDbConnector(""http://localhost:8086/", "ie3_in", null);
unparameterizedInfluxDb.equals(defaultInfluxDb); //true
```
