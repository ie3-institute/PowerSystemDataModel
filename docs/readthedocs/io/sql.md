# Sql


## Id Coordinate Source
The sql implementation of id coordinate source uses <a href="https://www.postgresql.org/">PostgreSql</a> with the
addon <a href="https://postgis.net/">PostGis</a>. `PostGis` is used to improve the querying of geographical data.
The `Coordinate` attribute is stored as a <a href="http://postgis.net/workshops/postgis-intro/geometries.html">geometry</a> (<a href="https://postgis.net/docs/ST_Point.html">point</a>).
