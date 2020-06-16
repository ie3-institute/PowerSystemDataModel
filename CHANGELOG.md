# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased/Snapshot]

### Added
-  Headline in csv files is now mandatory. CsvDataSource checks for existing field `uuid` in first row of .csv file
-  Minor logging improvements
-  New constructor in ``Transformer3WInput`` that allows the internal node to be marked as slack
-  Method in ``ContainerUtils`` to modify a provided ``SubGridContainer`` with slack nodes and make it usable for
most of the commonly known power flow calculations
-  gradle task to create JavaDoc HTML files in the folder 'docs/javadoc'
-  added missing ``HpResult`` model
-  Implementation of ``DataConnector`` and  ``WeatherSource`` for InfluxDB
-  Introduction of a ``IdCoordinateSource`` and implementation of  corresponding csv source for ID to coordinate mapping
-  Factory for ``TimeBasedValues<WeatherValue>``
-  Documentation with Sphinx / ReadTheDocs: [https://powersystemdatamodel.readthedocs.io/en/latest/](https://powersystemdatamodel.readthedocs.io/en/latest/)
-  Introduction of``SwitchResultFactory`` to build adapted ``SwitchResult`` entities
-  Copy method for all `RawGridElements` and `SystemParticipants` input entities which allow an easy to use entity copy with altered field values
-  ``distanceBetweenNodes(NodeInput nodeA, NodeInput nodeB)`` in ``GridAndGeoUtils``
-  Additional constructors based on lists of entities in ``RawGridElements``, ``SystemParticipants`` and ``GraphicElements``
-  Added ``DistanceWeightedGraph`` + corresponding utility method to generate a graph topology whose vertices are `NodeInput` entities and its edges are weighted with the distance between the vertices in meter
-  Added ``ContainerNodeUpdateUtil`` to support updating nested nodes in ``GridContainer`` instances
-  Gradle task `gradle finalizePR` to format and test the code as well as generate JavaDoc

### Changed
-  Disabled concurrent writing in `CsvFileSink.persistJointGrid()` as this caused concurrency issues
-  Modifications in `LineInput` and `GraphicInput` constructors to make `LineStrings` with two exactly equal
coordinates or multiple exactly equal coordinates possible
-  Extended functionality of `GridAndGeoUtils`
- `CsvFileConnector` is now set up to process either UniqueEntities or only by file name
- `SwitchResult` superclass changed from `ConnectorResult` to `ResultEntity`
-  Consolidate test tasks. `gradle allTests` is now replaced by `gradle test`. Only unit tests can be run with `gradle unitTest`. 

### Fixed
-  CsvDataSource now stops trying to get an operator for empty operator uuid field in entities
-  CsvDataSource now parsing multiple geoJson strings correctly
