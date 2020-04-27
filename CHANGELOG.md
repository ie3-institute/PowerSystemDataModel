# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Headline in csv files is now mandatory. CsvDataSource checks for existing field `uuid` in first row of .csv file
- Minor logging improvements

### Changed
- Disabled concurrent writing in `CsvFileSink.persistJointGrid()` as this caused concurrency issues

### Fixed
- CsvDataSource now stops trying to get an operator for empty operator uuid field in entities
- CsvDataSource now parsing multiple geoJson strings correctly
