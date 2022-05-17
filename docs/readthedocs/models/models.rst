################
Available models
################
This page gives an overview about all available models in *PowerSystemDataModel*.
They are basically grouped into three groups:

  1. `Input`_ models may be used to describe input data for a power system simulation
  2. `Result`_ models denote results of such a simulation
  3. `Time Series`_ may serve both as input or output

All those models are designed with some assumptions and goals in mind.
To assist you in applying them as intended, we will give you some general remarks:

Uniqueness
  All models have a :code:`uuid` field as universal unique identifier.
  There shouldn't be any two elements with the same :code:`uuid` in your grid data set, better in your whole collection
  of data sets.

Immutability
  We designed the models in a way, that does not allow for adaptions of the represented data after instantiation of the
  objects.
  Thereby you can be sure, that your models are *thread-safe* and no unwanted or unobserved changes are made.

Copyable
  With the general design principle of immutability, entity modifications (e.g. updates of field values) can become
  hard and annoying. To avoid generating methods to update each field value, we provide an adapted version of the
  `builder pattern <https://en.wikipedia.org/wiki/Builder_pattern/>`_ to make entity modifications as easy as possible.
  Each entity holds it's own copy builder class, which follows the same inheritance as the entity class itself. With a
  call of `.copy()` on an entity instance a builder instance is returned, that allows for modification of fields and
  can be terminated with `.build()` which will return an instance of the entity with modified field values as required.
  For the moment, this pattern is only implemented for a small amount of `AssetInput` entities (all entities held by a
  `GridContainer` except thermal units to be precise), but we plan to extend this capability to all input entities in the
  future.

Single Point of Truth
  Throughout all models you can be sure, that no information is given twice, reducing the possibility to have ambiguous
  information in your simulation set up.
  "Missing" information can be received through the grids relational information - e.g. if you intend to model a wind
  energy converter in detail, you may find information of it's geographical location in the model of it's common
  coupling point (:ref:`node<node_model>`).

Harmonized Units System
  As our models are representations of physical elements, we introduced a harmonized system of units.
  The standard units, the models are served with, is given on each element's page.
  Thereby you can be sure, that all information are treated the same.
  As most (database) sources do not support physical units, make sure, you have your input data transferred to correct
  units before.
  Same applies for interpreting the obtained results.
  In all models physical values are transferred to standard units on instantiation.

Equality Checks
  To represent quantities in the models within an acceptable accuracy, the JSR 385 reference implementation
  `Indriya <https://github.com/unitsofmeasurement/indriya>`_ is used. Comparing quantity objects or objects holding quantity
  instances is not as trivial as it might seem, because there might be different understandings about the equality of
  quantities (e.g. there is a big difference between two instances being equal or equivalent). After long discussions how to
  treat quantities in the entity :code:`equals()` method, we agreed on the following rules to be applied:

  - equality check is done by calling :code:`Objects.equals(<QuantityInstanceA>, <QuantityInstanceB>)` or
    :code:`<QuantityInstanceA>.equals(<QuantityInstanceB>)`.
    Using :code:`Objects.equals(<QuantityInstanceA>, <QuantityInstanceB>)` is necessary especially for time series data.
    As in contrast to all other places, quantity time series from real world data sometimes are not complete and
    hence contain missing values. To represent missing values this is the only place where the usage of :code:`null`
    is a valid choice and hence needs to be treated accordingly. Please remember that this is only allowed in very few
    places and you should try to avoid using :code:`null` for quantities or any other constructor parameter whenever possible!
  - equality is given if, and only if, the quantities value object and unit are exactly equal. Value objects can become
    e.g. :code:`BigDecimal` or :code:`Double` instances. It is important, that the object type is also the same, otherwise
    the entities :code:`equals()` method returns false. This behavior is in sync with the equals implementation
    of the indriya library. Hence, you should ensure that your code always pass in the same kind of a quantity instance
    with the same underlying number format and type. For this purpose you should especially be aware of the unit conversion
    method :code:`AbstractQuantity.to(Quantity)` which may return seemingly unexpected types, e.g. if called on a quantity
    with a :code:`double` typed value, it may return a quantity with a value of either :code:`Double` type or :code:`BigDecimal` type.
  - for now, there is no default way to compare entities in a 'number equality' way provided. E.g. a line with a length
    of 1km compared to a line with a length of 1000m is actually of the same length, but calling :code:`LineA.equals(LineB)`
    would return :code:`false` as the equality check does NOT convert units. If you want to compare two entity instances
    based on their equivalence you have (for now) check for each quantity manually using their :code:`isEquivalentTo()`
    method. If you think you would benefit from a standard method that allows entity equivalence check, please consider
    handing in an issue `here <https://github.com/ie3-institute/PowerSystemDataModel/issues>`_.
    Furthermore, the current existing implementation of :code:`isEquivalentTo()` in indriya does not allow the provision of
    a tolerance threshold that might be necessary when comparing values from floating point operations. We consider
    providing such a method in our `PowerSystemUtils <https://github.com/ie3-institute/PowerSystemUtils>`_ library.
    If you think you would benefit from such a method, please consider handing in an issue
    `here <https://github.com/ie3-institute/PowerSystemUtils/issues>`_.

Conditional Parameters
  Some of the models have conditional parameters. When reading model data from a data source, their respective factories for building these 
  models can handle nulls and empty Strings (as well as any combination of those) safely. E.g.: When given parameters for a line's 
  :code:`operationTime` where :code:`operationStartTime` and :code:`operationEndTime` are both :code:`null` or :code:`""`, the 
  factory will build an always-on line model.

*****
Input
*****
Model classes you can use to describe a data set as input to power system simulations.

.. toctree::
   :maxdepth: 1

   input/operator

Grid Related Models
===================
.. toctree::
   :maxdepth: 1

   input/grid/node
   input/grid/nodegraphic
   input/grid/line
   input/grid/linegraphic
   input/grid/switch
   input/grid/transformer2w
   input/grid/transformer3w
   input/grid/measurementunit
   input/grid/gridcontainer

Participant Related Models
==========================
.. toctree::
   :maxdepth: 1

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


Default Input Directory Hierarchy
===============================
.. figure:: PowerSystemDataModel/docs/uml/main/input/DefaultInputDirectoryHierarchy.puml
   :align: center
   :alt: Default input directory hierarchy



******
Result
******
Model classes you can use to describe the outcome of a power system simulation.

Grid Related Models
===================
.. toctree::
   :maxdepth: 1

   result/grid/node
   result/grid/connector
   result/grid/line
   result/grid/switch
   result/grid/transformer
   result/grid/transformer2w
   result/grid/transformer3w

Participant Related Models
==========================
.. toctree::
   :maxdepth: 1

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

***********
Time Series
***********
Time series are meant to represent a timely ordered series of values.
Those can either be electrical or non-electrical depending on what one may need for power system simulations.
Our time series models are divided into two subtypes:

.. _individual_time_series:

Individual Time Series
   Each time instance in this time series has its own value (random duplicates may occur obviously).
   They are only applicable for the time frame that is defined by the content of the time series.

.. _repetitive_time_series:

Repetitive Time Series
   Those time series do have repetitive values, e.g. each day or at any other period.
   Therefore, they can be applied to any time frame, as the mapping from time instant to value is made by information
   reduction.
   In addition to actual data, a mapping function has to be known.

To be as flexible, as possible, the actual content of the time series is given as children of the :code:`Value` class.
The following different values are available:

+-------------------------------+------------------------------------------------------------------+
| Value Class                   | Purpose                                                          |
+===============================+==================================================================+
| :code:`PValue`                | Electrical active power                                          |
+-------------------------------+------------------------------------------------------------------+
| :code:`SValue`                | Electrical active and reactive power                             |
+-------------------------------+------------------------------------------------------------------+
| :code:`HeatAndPValue`         | | Combination of thermal power (e.g. in kW)                      |
|                               | | and electrical active power (e.g. in kW)                       |
+-------------------------------+------------------------------------------------------------------+
| :code:`HeatAndSValue`         | | Combination of thermal power (e.g. in kW)                      |
|                               | | and electrical active and reactive power (e.g. in kW and kVAr) |
+-------------------------------+------------------------------------------------------------------+
| :code:`EnergyPriceValue`      | Wholesale market price (e.g. in € / MWh)                         |
+-------------------------------+------------------------------------------------------------------+
| :code:`SolarIrradianceValue`  | Combination of diffuse and direct solar irradiance               |
+-------------------------------+------------------------------------------------------------------+
| :code:`TemperatureValue`      | Temperature information                                          |
+-------------------------------+------------------------------------------------------------------+
| :code:`WindValue`             | Combination of wind direction and wind velocity                  |
+-------------------------------+------------------------------------------------------------------+
| :code:`WeatherValue`          | Combination of irradiance, temperature and wind information      |
+-------------------------------+------------------------------------------------------------------+

.. include:: ValidationUtils.rst

