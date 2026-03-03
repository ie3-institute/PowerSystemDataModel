# Field naming

For each entity and model the data model defines some fields that are needed for building them. The fields can be categorized as:
1. Mandatory fields: These fields needs to be provided by the source in order to build a model.
2. Optional fields: These fields can be present to provide additional information, but they are not required to build the entity.
3. Unsupported fields: Some entities or value may define fields that are explicitly not supported.

If a source is providing fields that are neither defined as mandatory or optional nor are explicitly unsupported, they
are ignored while building the model.


## Defining fields

In order to define the mandatory, optional and unsupported fields for an entity or a value, the class
`edu.ie3.datamodel.io.naming.ModelFields` is used. This class stores all known fields globally and provides methods for
adding new models to these stores.



## Default fields


### Models

For the models please refer to the [model pages](/models/models).


### Id Coordinate
Csv id coordinate sources can have two different ways to represent their coordinate:
1. ICON: Takes a `latitude` and a `longitude` column
2. COSMO: Takes a `lat_rot`, a `long_rot`, a `lat_geo` and a `long_geo` column

#### Individual Time Series

The following keys are supported until now:
```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1

   * - Key
     - Information
     - Supported head line
   * - c
     - An energy price (e.g. in €/MWh; c stands for charge).
     - ``time,price``
   * - p
     - Active power.
     - ``time,p``
   * - pq
     - Active and reactive power.
     - ``time,p,q``
   * - h
     - Heat power demand.
     - ``time,h``
   * - ph
     - Active and heat power.
     - ``time,p,h``
   * - pqh
     - Active, reactive and heat power.
     - ``time,p,q,h``
   * - v
     - Voltage mangnitude in pu and angle in °.
     - ``time,vMag,vAng``
   * - weather
     - Weather information.
     - ``time,coordinate,direct_irradiation,diffuse_irradiation,temperature,wind_velocity,wind_direction``

```

##### Load Profile Time Series

The following profiles are supported until now:
```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1

   * - Key
     - Information
     - Supported head line
   * - e.g.: H0
     - BDEW standard load profiles 1999 ([source](https://www.bdew.de/energie/standardlastprofile-strom/))
     - ``SuSa,SuSu,SuWd,TrSa,TrSu,TrWd,WiSa,WiSu,WiWd,quarterHour``
   * - e.g.: h25
     - BDEW standard load profiles 2025 ([source](https://www.bdew.de/energie/standardlastprofile-strom/))
     - ``janSa,janSu,janWd,febSa,febSu,febWd,marSa,marSu,marWd,aprSa,aprSu,aprWd,maySa,maySu,mayWd,junSa,junSu,junWd,julSa,julSu,julWd,augSa,augSu,augWd,sepSa,sepSu,sepWd,octSa,octSu,octWd,novSa,novSu,novWd,decSa,decSu,decWd,quarterHour``
   * - random
     - A random load proile based on: ``Kays - Agent-based simulation environment for improving the planning of distribution grids``
     - ``kSa,kSu,kWd,mySa,mySu,myWd,sigmaSa,sigmaSu,sigmaWd,quarterHour``

```

