# SQL


## Id Coordinate Source
The sql implementation of id coordinate source uses [PostgreSql](https://www.postgresql.org/) with the
addon [PostGis](https://postgis.net/). `PostGis` is used to improve the querying of geographical data.
The `Coordinate` attribute is stored as a [Geography](http://postgis.net/workshops/postgis-intro/geography.html) with
the type [Point](https://postgis.net/docs/ST_Point.html) and the default SRID 4326.

## Export

To export weather and coordinate tables to CSV, use the following queries:

```sql
COPY (
SELECT
	to_char(time, 'YYYY-MM-DD"T"HH24:MI:SS"Z"') AS time,
	coordinate_id,
	aswdifd_s,
	aswdir_s,
	t2m,
	u131m,
	v131m
FROM weathervalue
) TO '/tmp/weather.csv' CSV HEADER
```

```sql
COPY (
SELECT
	id,
	ST_X (coordinate::geometry) AS longitude,
	ST_Y (coordinate::geometry) AS latitude,
	'ICON' as coordinate_type
FROM coordinate
) to '/tmp/coordinates.csv' CSV HEADER
```
