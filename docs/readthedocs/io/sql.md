# SQL


## Id Coordinate Source
The sql implementation of id coordinate source uses [PostgreSql](https://www.postgresql.org/) with the
addon [PostGis](https://postgis.net/). `PostGis` is used to improve the querying of geographical data.
The `Coordinate` attribute is stored as a [Geography](http://postgis.net/workshops/postgis-intro/geography.html) with
the type [Point](https://postgis.net/docs/ST_Point.html) and the default SRID 4326.
