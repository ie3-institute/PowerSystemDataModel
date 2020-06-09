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

+--------------------------+------------------------------------------------------------------+
| Value Class              | Purpose                                                          |
+==========================+==================================================================+
| :code:`PValue`           | Electrical active power                                          |
+--------------------------+------------------------------------------------------------------+
| :code:`SValue`           | Electrical active and reactive power                             |
+--------------------------+------------------------------------------------------------------+
| :code:`HeatAndPValue`    | | Combination of thermal power (e.g. in kW)                      |
|                          | | and electrical active power (e.g. in kW)                       |
+--------------------------+------------------------------------------------------------------+
| :code:`HeatAndSValue`    | | Combination of thermal power (e.g. in kW)                      |
|                          | | and electrical active and reactive power (e.g. in kW and kVAr) |
+--------------------------+------------------------------------------------------------------+
| :code:`EnergyPriceValue` | Wholesale market price (e.g. in € / MWh)                         |
+--------------------------+------------------------------------------------------------------+
| :code:`IrradiationValue` | Combination of diffuse and direct solar irradiation              |
+--------------------------+------------------------------------------------------------------+
| :code:`TemperatureValue` | Temperature information                                          |
+--------------------------+------------------------------------------------------------------+
| :code:`WindValue`        | Combination of wind direction and wind velocity                  |
+--------------------------+------------------------------------------------------------------+
| :code:`WeatherValue`     | Combination of irradiation, temperature and wind information     |
+--------------------------+------------------------------------------------------------------+
