# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased/Snapshot]

### Added
- Formatting Spotless Groovy import order [#960](https://github.com/ie3-institute/PowerSystemDataModel/issues/960)
- Implementing missing typical methods in `Try` [#970]()
- Added log warning when using `SwitchInputs` with `parallelDevices` parameter [#840](https://github.com/ie3-institute/PowerSystemDataModel/issues/840)

### Fixed
- Fixed Couchbase integration tests that randomly failed [#755](https://github.com/ie3-institute/PowerSystemDataModel/issues/755)
- Fixed hyperlink in line documentation [#965](https://github.com/ie3-institute/PowerSystemDataModel/issues/965)

### Changed
- Changing from comparing strings to comparing uuids in `EntitySource.findFirstEntityByUuid` [#829](https://github.com/ie3-institute/PowerSystemDataModel/issues/829)
- Adding JavaDoc to `EntitySource.safeMapGet` [#828](https://github.com/ie3-institute/PowerSystemDataModel/issues/828)
- Abstracting some methods in `ValidationUtils` [#852](https://github.com/ie3-institute/PowerSystemDataModel/issues/852)
- `EmInput` should not be connected to the grid [#955](https://github.com/ie3-institute/PowerSystemDataModel/issues/955)
- Enhancing the error message for coordinate sources with invalid column names [#670](https://github.com/ie3-institute/PowerSystemDataModel/issues/670) 
- Allowing for additional unused columns in sources [#839](https://github.com/ie3-institute/PowerSystemDataModel/issues/839)
- Improving column name validation to only run once per source [#849](https://github.com/ie3-institute/PowerSystemDataModel/issues/849)
- Refactored and abstracted `EntitySource`s and `EntityData` creation [#969](https://github.com/ie3-institute/PowerSystemDataModel/issues/969)

## [4.1.0] - 2023-11-02

### Fixed
- Making constructor of `CsvDataSource` public [#894](https://github.com/ie3-institute/PowerSystemDataModel/issues/894)
- Fixing spotless groovy [#918](https://github.com/ie3-institute/PowerSystemDataModel/issues/918)

### Changed
- `SqlIdCoordinateSource.createCooridinateValue` now throws an exception when the coordinate can not be built [#911](https://github.com/ie3-institute/PowerSystemDataModel/issues/911)
- CleanUp `BufferedCsvWriterTest` only after all tests are completed [#809](https://github.com/ie3-institute/PowerSystemDataModel/issues/809)
- Update gradle to version 8.4 [#891](https://github.com/ie3-institute/PowerSystemDataModel/issues/891)

## [4.0.0] - 2023-08-01

### Added
- Copy methods for container classes [#726](https://github.com/ie3-institute/PowerSystemDataModel/issues/726)
- Allow hierarchic grid structure for JointGridContainer [#768](https://github.com/ie3-institute/PowerSystemDataModel/issues/768)
- Adding SQL id coordinate sources (``IdCoordinateSource``) [#689](https://github.com/ie3-institute/PowerSystemDataModel/issues/689)
- Added some standard asset types to documentation [#642](https://github.com/ie3-institute/PowerSystemDataModel/issues/642)

### Fixed
- Fixed wrong rated power unit hint [#804](https://github.com/ie3-institute/PowerSystemDataModel/issues/804)
- Fixed wrong hash code generation of ConnectorResult [#817](https://github.com/ie3-institute/PowerSystemDataModel/issues/817) 

### Changed
- Removing deprecated classes and methods [#540](https://github.com/ie3-institute/PowerSystemDataModel/issues/540)
- Refactor CSV data sources [#716](https://github.com/ie3-institute/PowerSystemDataModel/issues/716)
- Deleted parameter initFiles, set parameter append to false by default [#791](https://github.com/ie3-institute/PowerSystemDataModel/issues/791)
- Use nio paths instead of strings for file path [#723](https://github.com/ie3-institute/PowerSystemDataModel/issues/723)
- Data source will throw an exceptions instead of returning an empty optionals [#707](https://github.com/ie3-institute/PowerSystemDataModel/issues/707)
- Improving `ValidationUtils` [#758](https://github.com/ie3-institute/PowerSystemDataModel/issues/758)

## [3.0.0] - 2023-02-16

### Added
- SQL time series sources (`SqlTimeSeriesSource` and `SqlTimeSeriesMappingSource`) [#467](https://github.com/ie3-institute/PowerSystemDataModel/issues/467)
- SQL time series have a different structure than CSV counterparts [#545](https://github.com/ie3-institute/PowerSystemDataModel/issues/545)
- Graph with impedance weighted edges including facilities to create it [#440](https://github.com/ie3-institute/PowerSystemDataModel/issues/440)
- `TimeSeriesMetaInformationSource` providing a source for the mapping of time series uuids to column schemes (previously provided by `TimeSeriesMappingSource`) [#515](https://github.com/ie3-institute/PowerSystemDataModel/issues/515)
- `TemperatureDependantLoadProfile`s for depiction of profile behavior of night storage heating and heat pumps [#601](https://github.com/ie3-institute/PowerSystemDataModel/issues/601)
- `ThermalUnits` as a container to hold all thermal units [#134](https://github.com/ie3-institute/PowerSystemDataModel/issues/134)
- `ThermalInput` as a distinct abstract class for all thermal models
- `ThermalGrid` as a container for a completely connected thermal grid
- `EmResult` and `FlexOptionsResult` for Energy Management Systems [#651](https://github.com/ie3-institute/PowerSystemDataModel/issues/651)
- `EvcsInput` now has a parameter for enabling and disabling vehicle to grid support [#681](https://github.com/ie3-institute/PowerSystemDataModel/issues/681)
- Added Dependabot updates to sphinx/readthedocs dependencies [#735](https://github.com/ie3-institute/PowerSystemDataModel/issues/735)
- Created convenience function for JointGridContainer from CSV [#502](https://github.com/ie3-institute/PowerSystemDataModel/issues/502)
- Added CSV grid IO integration test [#586](https://github.com/ie3-institute/PowerSystemDataModel/issues/586)

### Fixed
- Reduced code smells [#492](https://github.com/ie3-institute/PowerSystemDataModel/issues/492)
  - Protected constructors for abstract classes
  - Use pattern matching
  - Remove unused imports
  - Use enhanced switch statements
  - Replace lambdas with method references
  - Use `Stream#toList`
  - Adapt visibility for JUnit 5
- More code smell fixing [#633](https://github.com/ie3-institute/PowerSystemDataModel/issues/633)
  - Use `List#of`
  - Use direct assignment with switch/case structures
  - Turn some classes into records
  - Making abstract classes' constructor protected
  - Improving some RegExs
  - Replacing `filter(Optional::isPresent).map(Optional::get)` on streams with `flatMap(Optional::stream)`
  - instanceof variable declarations
  - Removing unnecessary parentheses
  - Miscellaneous code smells
- Fix JavaDoc creation
  - Create JavaDoc with java 17 instead of java 8
  - Let JavDoc pass, if there are warnings **ATTENTION:** Should be removed, when JavaDoc is fixed! (cf. Issue [#494](https://github.com/ie3-institute/PowerSystemDataModel/issues/494))
- `BufferedCsvWriter` writes columns in the order, that the headline elements are defined [#434](https://github.com/ie3-institute/PowerSystemDataModel/issues/393)
- Cleaned up `IndividualTimeSeriesMetaInformation`-related methods in `CsvFileConnector` [#544](https://github.com/ie3-institute/PowerSystemDataModel/issues/544)
- Fixed spotlessApply handling for `.groovy` files [#637](https://github.com/ie3-institute/PowerSystemDataModel/issues/637)
- Re-using SQL connection per default [#653](https://github.com/ie3-institute/PowerSystemDataModel/issues/653)
- Persisting EmInputs [#665](https://github.com/ie3-institute/PowerSystemDataModel/issues/665)
- Charging point type parsing now works with more id definitions [#686](https://github.com/ie3-institute/PowerSystemDataModel/issues/685)
- Fix `EvResult.toString` [#690](https://github.com/ie3-institute/PowerSystemDataModel/issues/690)

### Changed
- BREAKING: PvInput Model parameter name height changed to elevationAngle [#393](https://github.com/ie3-institute/PowerSystemDataModel/issues/393) :warning:
- BREAKING: Transformer's no load susceptance needs to be zero or negative to pass model validation [#378](https://github.com/ie3-institute/PowerSystemDataModel/issues/378)
  - All input data sets for version < 3.0.0 need to be altered!
- Deprecating (as part of [#513](https://github.com/ie3-institute/PowerSystemDataModel/issues/513)): 
  - `edu.ie3.datamodel.io.csv.timeseries.ColumnScheme`
  - `edu.ie3.datamodel.io.csv.FileNameMetaInformation`
  - `edu.ie3.datamodel.io.csv.timeseries.IndividualTimeSeriesMetaInformation`
  - `edu.ie3.datamodel.io.csv.timeseries.LoadProfileTimeSeriesMetaInformation`
  - `edu.ie3.datamodel.io.connectors.CsvFileConnector.CsvIndividualTimeSeriesMetaInformation`
  - and related methods
- BREAKING: Comprehensive harmonization around weather sources [#267](https://github.com/ie3-institute/PowerSystemDataModel/issues/267)
  - Adapted the expected column scheme
    - General weather model
      - `coordinate` to `coordinateid`
    - DWD COSMO model
      - `diffuseirradiation` to `diffuseirradiance`
      - `directirradiation` to `directirradiance`
    - ICON model:
      - `"datum"` to `"time"`
  - Force user to provide time stamp pattern to `CouchbaseWeatherSource` to ensure harmonized querying
- BREAKING: Updating PowerSystemUtils dependency to 2.0-SNAPSHOT [#595](https://github.com/ie3-institute/PowerSystemDataModel/issues/595)
- BREAKING: Generified the `LoadInput` attribute `standardLoadProfile` to `loadProfile` as it should also address the newly added `TemperatureDependantLoadProfile`s [#601](https://github.com/ie3-institute/PowerSystemDataModel/issues/601)
- Adapted to new double converters in PSU [#705](https://github.com/ie3-institute/PowerSystemDataModel/issues/705)
- Setting fixed groovy version and updating groovy [#788](https://github.com/ie3-institute/PowerSystemDataModel/issues/788)

## [2.1.0] - 2022-01-05

### Added
- added `EvcsLocationType` support in `EvcsInput` and `EvcsInputFactory` [#406](https://github.com/ie3-institute/PowerSystemDataModel/issues/406)
- Opportunity to close writer in `CsvFileSink`
- Generified SQL data sources for future extensions

### Fixed
- adapted `LineInput` constructor to convert line length to `StandardUnits.LINE_LENGTH` [#412](https://github.com/ie3-institute/PowerSystemDataModel/issues/412)

### Changed
- Writers used to write time series are closed right away
- Changed class name in FlexOptionsResult.toString [#693](https://github.com/ie3-institute/PowerSystemDataModel/issues/693)
- Deleted parameter decimalPlaces and changed naming of serialization method [#710](https://github.com/ie3-institute/PowerSystemDataModel/issues/710)
- Changed switch result documentation according to the implementation [#757](https://github.com/ie3-institute/PowerSystemDataModel/issues/757)
- Added documentation for EmResult and FlexOptionResult [#656](https://github.com/ie3-institute/PowerSystemDataModel/issues/656)
- Added method that checks if the transformer nodes are located on the correct voltage side [#803](https://github.com/ie3-institute/PowerSystemDataModel/issues/803)

## [2.0.1] - 2021-07-08

### Fixed
- fix CHANGELOG.md
- replace `LogManager` calls with `LogFactory` for facade logging support

## [2.0.0] - 2021-05-21

### Added
-   added `ResultEntitySource` interface
-   added `CsvResultEntitySource` implementation to read `ResultEntity` instances from .csv files
-   added target temperature including tolerance boundaries to `ThermalHouseInput`

### Changed
-   separated entity and file naming and introduced a new FileNamingStrategy taking an EntityNamingStrategy and a FileHierarchy as arguments

### Fixed
-   `CsvSystemParticipantSource#getSystemParticipants()` now correctly returns electric vehicle charging station input models [PR#370](https://github.com/ie3-institute/PowerSystemDataModel/pull/370)

## [2.0.0] - 2021-05-21

### Added
-   definition for a default input file directory structure
-   tarball utils to extract and compress files
-   added electric vehicle charging station implementation ``EvcsInput``
-   reading time series from csv files (including a container object to hold the different types of time series)
-   reading mapping from participant uuid to time series uuid including a mapping object for easy access to time series
-   Couchbase, SQL and CSV connectors and sources for weather data
-   added validation utils that can be used to check whether objects have valid values (no usage implemented yet)
-   added `SystemParticipantWithHeatResult` with thermal power variable to be used by heat plant result models

### Changed
-   BREAKING: replaced [Unit API 1.0](https://github.com/unitsofmeasurement/uom-se) (JSR 363, tec.uom.se) with [Unit API 2.0](https://github.com/unitsofmeasurement/indriya) (JSR 385, tech.units.indriya)
-   added possibility to allow `null` values in time series for missing values (e.g. if some measure data points from real world data time series are missing)
-   moved api docs to own branch incl. automated api-docs deployment
-   added methods for nearest and all coordinates to IdCoordinateSource
-   utilize factory in IdCoordinateSource to maintain highest possible flexibility
-   added coordinate distance sort method to GridAndGeoUtils
-   BREAKING: Harmonized field naming for time information
-   BREAKING: Properly applying snake case to result file names
-   deprecated `TarballUtils`
-   updated Indriya to version 2.1.2 to include fixes for serialization
-   Reworking the time series source (one source per time series, distinct mapping source, factory pattern)
-   BREAKING: Moved methods `buildSafe{Coord,Point,LineString,LineStringBetweenCoords,LineStringBetweenPoints}`, `totalLengthOfLineString` from `GridAndGeoUtils` to `GeoUtils` in [_PowerSystemUtils_](https://github.com/ie3-institute/PowerSystemUtils)
-   BREAKING: Moved `CoordinateDistance` to [_PowerSystemUtils_](https://github.com/ie3-institute/PowerSystemUtils)
-   Factory methods for `SubGridGate`
-   BREAKING: Inheritance hierarchy of exceptions all around entity validation

### Removed
-   BREAKING: Removed deprecated code parts
	-   Intermingled builder pattern and constructors in `SubGridGate`
	-   `TarballUtils` that have been transferred to `FileIOUtils` in [_PowerSystemUtils_](https://github.com/ie3-institute/PowerSystemUtils)
	-   `FileNamingStrategy` that has been transferred to `EntityPersistenceNamingStrategy`
	-   `EvCharacteristicInput` and `TimeSeriesContainer` that shouldn't be used anymore

### Fixed
-   InfluxDbConnector now keeps session instead of creating a new one each call
(resolves [#247](https://github.com/ie3-institute/PowerSystemDataModel/issues/247)
and [#248](https://github.com/ie3-institute/PowerSystemDataModel/issues/248))
-   BREAKING: fix invalid application of solar irradiance / irradiation ([#266](https://github.com/ie3-institute/PowerSystemDataModel/issues/266))
-   BREAKING: deleted `IrradiationValue` as it is invalid and no longer required
-   added copy builder implementations for thermal input models `CylindricalStorageInput`, `ThermalBusInput` and `ThermalHouseInput`
-   the sample code for reading and writing model data from respectively to csv files documented in the Sphinx documentation is fixed

## [1.1.0] - 2020-09-15

### Added
-   Headline in csv files is now mandatory. CsvDataSource checks for existing field `uuid` in first row of .csv file
-   Minor logging improvements
-   New constructor in ``Transformer3WInput`` that allows the internal node to be marked as slack
-   Method in ``ContainerUtils`` to modify a provided ``SubGridContainer`` with slack nodes and make it usable for
most of the commonly known power flow calculations
-   gradle task to create JavaDoc HTML files in the folder 'docs/javadoc'
-   added missing ``HpResult`` model
-   Implementation of ``DataConnector``, ``WeatherSource`` and ``DataSink`` for InfluxDB
-   Introduction of a ``IdCoordinateSource`` and implementation of  corresponding csv source for ID to coordinate mapping
-   Factory for ``TimeBasedValues<WeatherValue>``
-   Documentation with Sphinx / ReadTheDocs: [https://powersystemdatamodel.readthedocs.io/en/latest/](https://powersystemdatamodel.readthedocs.io/en/latest/)
-   Introduction of``SwitchResultFactory`` to build adapted ``SwitchResult`` entities
-   Copy method for all `RawGridElements` and `SystemParticipants` input entities which allow an easy to use entity copy with altered field values
-   ``distanceBetweenNodes(NodeInput nodeA, NodeInput nodeB)`` in ``GridAndGeoUtils``
-   Additional constructors based on lists of entities in ``RawGridElements``, ``SystemParticipants`` and ``GraphicElements``
-   Added ``DistanceWeightedGraph`` + corresponding utility method to generate a graph topology whose vertices are `NodeInput` entities and its edges are weighted with the distance between the vertices in meter
-   Added ``ContainerNodeUpdateUtil`` to support updating nested nodes in ``GridContainer`` instances
-   Gradle task `gradle finalizePR` to format and test the code as well as generate JavaDoc

### Changed
-   Disabled concurrent writing in `CsvFileSink.persistJointGrid()` as this caused concurrency issues
-   Modifications in `LineInput` and `GraphicInput` constructors to make `LineStrings` with two exactly equal
coordinates or multiple exactly equal coordinates possible
-   Extended functionality of `GridAndGeoUtils`
- `CsvFileConnector` is now set up to process either UniqueEntities or only by file name
- `SwitchResult` superclass changed from `ConnectorResult` to `ResultEntity`
- ``CsvDataSource`` now parses valid RFC 4180 rows correctly (invalid, old syntax is still supported but deprecated!)
-   Consolidate test tasks. `gradle allTests` is now replaced by `gradle test`. Only unit tests can be run with `gradle unitTest`.
- Changed projects toString() methods for readability/completeness
- Adapted to changes in PowerSystemUnits in PowerSystemUtils [#631](https://github.com/ie3-institute/PowerSystemDataModel/issues/631)

### Fixed
-   CsvDataSource now stops trying to get an operator for empty operator uuid field in entities
-   CsvDataSource now parsing multiple geoJson strings correctly

[Unreleased/Snapshot]: https://github.com/ie3-institute/powersystemdatamodel/compare/4.1.0...HEAD
[4.1.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/4.0.0...4.1.0
[4.0.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/3.0.0...4.0.0
[3.0.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/2.1.0...3.0.0
[2.1.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/2.0.1...2.1.0
[2.0.1]: https://github.com/ie3-institute/powersystemdatamodel/compare/2.0.0...2.0.1
[2.0.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/1.1.0...2.0.0
[1.1.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/6a49bc514be8859ebd29a3595cd58cd000498f1e...1.1.0
