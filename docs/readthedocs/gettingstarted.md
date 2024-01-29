# Getting started
Welcome, dear fellow of bottom up power system modelling!
This section is meant to give you some help getting hands on our project.
If you feel, something is missing, please contact us!


## Requirements

Java > v 17

## Where to get

Checkout latest from [GitHub](https://github.com/ie3-institute/PowerSystemDataModel) or use maven for dependency
management:

### Stable releases

On [Maven central](https://search.maven.org/artifact/com.github.ie3-institute/PowerSystemDataModel):

```xml
<dependency>
  <groupId>com.github.ie3-institute</groupId>
  <artifactId>PowerSystemDataModel</artifactId>
  <version>2.1.0</version>
</dependency>
```


### Snapshot releases

Available on [OSS Sonatype](https://s01.oss.sonatype.org/).
Add the correct repository:

```xml
<repositories>
  <repository>https://s01.oss.sonatype.org/content/repositories/snapshots</repository>
</repositories>
```

and add the dependency:

```xml
<dependency>
  <groupId>com.github.ie3-institute</groupId>
  <artifactId>PowerSystemDataModel</artifactId>
  <version>3.0-SNAPSHOT</version>
</dependency>
```

## Important changes

With the release of `PowerSystemDataModel` version `5.0` the support for the old csv file format will be fully removed.
It was already marked as `deprecated` back in version `1.1.0`. For those who are still using a model in the old csv format
the following guide will provide a fast and easy way to convert old format into the new one.

- Since the support is removed in version `5.0`, the `PowerSystemDataModel` version `3.x` or `4.x` must be 
  used to read the old format. The `PSDM` will automatically write the output model in the new csv format.


``` java
    /* Parameterization */
    String gridName = "gridWithOldFormat";
    String csvSep = ",";
    Path folderPath = Path.of("PATH_TO_THE_FOLDER");
    boolean isHierarchic = false;
    Path output = Path.of("PATH_OF_THE_OUTPUT_FOLDER");
    FileNamingStrategy namingStrategy = new FileNamingStrategy();
    
    /* Reading the old format */
    JointGridContainer container = CsvJointGridContainerSource.read(gridName, csvSep, folderPath, isHierarchic);
    
    /* Writing in the new format */
    CsvFileSink sink = new CsvFileSink(output, namingStrategy, csvSep);
    sink.persistJointGrid(container);
```


