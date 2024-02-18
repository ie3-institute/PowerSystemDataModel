# Available models
This page gives an overview about all available models in *PowerSystemDataModel*.
They are basically grouped into two groups:

1. [Input](#input) models may be used to describe input data for a power system simulation
2. [Result](#result) models denote results of such a simulation

All those models are designed with some assumptions and goals in mind.
To assist you in applying them as intended, we will give you some general remarks:

**Uniqueness** <br>
All models have a `uuid` field as universal unique identifier.
There shouldn't be any two elements with the same `uuid` in your grid data set, better in your whole collection
of data sets.

**Immutability** <br>
We designed the models in a way, that does not allow for adaptions of the represented data after instantiation of the
objects.
Thereby you can be sure, that your models are *thread-safe* and no unwanted or unobserved changes are made.

**Clonability** <br>
With the general design principle of immutability, entity modifications (e.g. updates of field values) can become
hard and annoying. To avoid generating methods to update each field value, we provide an adapted version of the
[Builder pattern](https://en.wikipedia.org/wiki/Builder_pattern/) to make entity modifications as easy as possible.
Each entity holds its own copy builder class, which follows the same inheritance as the entity class itself. With a
call of `.copy()` on an entity instance a builder instance is returned, which allows for modification of fields and
can be terminated with `.build()`. This will return an instance of the entity with modified field values as indicated.
For the moment, this pattern is only implemented for a limited set of entities, but we plan to extend this capability 
to all input entities in the future.

**Scaling entity properties** <br>
Using the copy builders (as described above) we provide a convenience method that helps with scaling system 
participants and respective type inputs. Scaling entities tries to preserve proportions that are related to power. 
This means that capacity, consumption etc. are scaled with the same factor as power.

**Single Point of Truth** <br>
Throughout all models you can be sure, that no information is given twice, reducing the possibility to have ambiguous
information in your simulation set up.
"Missing" information can be received through the grids relational information - e.g. if you intend to model a wind
energy converter in detail, you may find information of it's geographical location in the model of it's common
coupling point ([node](/models/input/grid/node)).

**Harmonized Units System** <br>
As our models are representations of physical elements, we introduced a harmonized system of units.
The standard units, the models are served with, is given on each element's page.
Thereby you can be sure, that all information are treated the same.
As most (database) sources do not support physical units, make sure, you have your input data transferred to correct
units before.
Same applies for interpreting the obtained results.
In all models physical values are transferred to standard units on instantiation.

**Equality Checks** <br>
To represent quantities in the models within an acceptable accuracy, the JSR 385 reference implementation
[Indriya](https://github.com/unitsofmeasurement/indriya) is used. Comparing quantity objects or objects holding quantity
instances is not as trivial as it might seem, because there might be different understandings about the equality of
quantities (e.g. there is a big difference between two instances being equal or equivalent). After long discussions how to
treat quantities in the entity `equals()` method, we agreed on the following rules to be applied:

- equality check is done by calling `Objects.equals(<QuantityInstanceA>, <QuantityInstanceB>)` or
  `<QuantityInstanceA>.equals(<QuantityInstanceB>)`.
  Using `Objects.equals(<QuantityInstanceA>, <QuantityInstanceB>)` is necessary especially for time series data.
  As in contrast to all other places, quantity time series from real world data sometimes are not complete and
  hence contain missing values. To represent missing values this is the only place where the usage of `null`
  is a valid choice and hence needs to be treated accordingly. Please remember that this is only allowed in very few
  places and you should try to avoid using `null` for quantities or any other constructor parameter whenever possible!
- equality is given if, and only if, the quantities value object and unit are exactly equal. Value objects can become
  e.g. `BigDecimal` or `Double` instances. It is important, that the object type is also the same, otherwise
  the entities `equals()` method returns false. This behavior is in sync with the equals implementation
  of the indriya library. Hence, you should ensure that your code always pass in the same kind of a quantity instance
  with the same underlying number format and type. For this purpose you should especially be aware of the unit conversion
  method `AbstractQuantity.to(Quantity)` which may return seemingly unexpected types, e.g. if called on a quantity
  with a `double` typed value, it may return a quantity with a value of either `Double` type or `BigDecimal` type.
- for now, there is no default way to compare entities in a 'number equality' way provided. E.g. a line with a length
  of 1km compared to a line with a length of 1000m is actually of the same length, but calling `LineA.equals(LineB)`
  would return `false` as the equality check does NOT convert units. If you want to compare two entity instances
  based on their equivalence you have (for now) check for each quantity manually using their `isEquivalentTo()`
  method. If you think you would benefit from a standard method that allows entity equivalence check, please consider
  handing in an issue [Issues](https://github.com/ie3-institute/PowerSystemDataModel/issues).
  Furthermore, the current existing implementation of `isEquivalentTo()` in indriya does not allow the provision of
  a tolerance threshold that might be necessary when comparing values from floating point operations. We consider
  providing such a method in our [PowerSystemUtils](https://github.com/ie3-institute/PowerSystemUtils) library.
  If you think you would benefit from such a method, please consider handing in an issue
  [Issues](https://github.com/ie3-institute/PowerSystemUtils/issues).

**Conditional Parameters** <br>
Some of the models have conditional parameters. When reading model data from a data source, their respective factories for building these
models can handle nulls and empty Strings (as well as any combination of those) safely. E.g.: When given parameters for a line's
`operationTime` where `operationStartTime` and `operationEndTime` are both `null` or `""`, the
factory will build an always-on line model.

**Validation** <br>
Information regarding validation of models can be found [here](/io/ValidationUtils).


## Input
Model classes you can use to describe a data set as input to power system simulations.

```{toctree}
---
maxdepth: 1
---
input/operator
```

### Grid Related Models

```{toctree}
---
maxdepth: 1
---
input/grid/node
input/grid/nodegraphic
input/grid/line
input/grid/linegraphic
input/grid/switch
input/grid/transformer2w
input/grid/transformer3w
input/grid/measurementunit
input/grid/gridcontainer
```

### Participant Related Models

```{toctree}
---
maxdepth: 1
---
input/participant/general
input/participant/bm
input/participant/chp
input/participant/ev
input/participant/evcs
input/participant/fixedfeedin
input/participant/hp
input/participant/load
input/participant/pv
input/participant/storage
input/participant/wec
input/participant/thermalbus
input/participant/thermalhouse
input/participant/cylindricalstorage
input/participant/em
```

### Additional Data
Some models can use additional data for their calculations.

```{toctree}
---
maxdepth: 1
---
input/additionaldata/timeseries
input/additionaldata/idcoordinatesource
```

## Result
Model classes you can use to describe the outcome of a power system simulation.

### Grid Related Models

```{toctree}
---
maxdepth: 1
---
result/grid/node
result/grid/connector
result/grid/line
result/grid/switch
result/grid/transformer
result/grid/transformer2w
result/grid/transformer3w
```

### Participant Related Models

```{toctree}
---
maxdepth: 1
---
result/participant/bm
result/participant/chp
result/participant/ev
result/participant/evcs
result/participant/fixedfeedin
result/participant/hp
result/participant/load
result/participant/pv
result/participant/storage
result/participant/wec
result/participant/thermalsink
result/participant/thermalstorage
result/participant/thermalunit
result/participant/thermalhouse
result/participant/cylindricalstorage
result/participant/systemparticipant
result/participant/flexoption
result/participant/em
```
