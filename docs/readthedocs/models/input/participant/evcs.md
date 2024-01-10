(evcs-model)=

# Electric Vehicle Charging Station

Model of a charging station for electric vehicles. This model only covers the basic characteristics of a charging
station and has some limitations outlined below.

## Model Definition

### Entity Model

| Attribute        | Unit | Remarks                                                                              |
| ---------------- | ---- |--------------------------------------------------------------------------------------|
| uuid             | --   |                                                                                      |
| id               | --   | Human readable identifier                                                            |
| operator         | --   |                                                                                      |
| operationTime    | --   | Timely restriction of operation                                                      |
| node             | --   |                                                                                      |
| qCharacteristics | --   | [Reactive power characteristic](general.md#reactive-power-characteristics) to follow |
| type             | --   | [Charging point type](#charging-point-types) (valid for all installed points)        |
| chargingPoints   | --   | no of installed charging points @ the specific station                               |
| cosPhiRated      | --   | Rated power factor                                                                   |
| locationType     | --   | [Charging station location types](#location-types)                                   |

### Type Model

In contrast to other models, electric vehicle charging station types are not configured via separate type file or table,
but 'inline' of a charging station entry. This is justified by the fact, that the station type (in contrast to e.g.
the type of a wind energy converter) only consists of a few, more or less standardized parameters, that are (most of the
time) equal for all manufacturers. Hence, to simplify the type model handling, types are provided either by a string
literal of their id or by providing a custom one. See [Charging point types](evcs.md#charging-point-types) for details of on
available standard types and how to use custom types.

The actual model definition for charging point types looks as follows:

| Attribute           | Unit | Remarks                                       |
| ------------------- | ---- | --------------------------------------------- |
| id                  | --   | Human readable identifier                     |
| sRated              | kVA  | Rated apparent power                          |
| electricCurrentType | --   | Electric current type                         |
| synonymousIds       | --   | Set of alternative human readable identifiers |

(evcs-point-types)=

## Charging Point Types

### Available Standard Types

To simplify the application of electric vehicle charging stations, some common standard types are available out-of-the-box.
They can either by used code wise or directly from database or file input by referencing their id or one of their
synonymous ids. All standard types can be found in {code}`edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointTypeUtils`.

| id                           | synonymous ids                               | sRated in kVA | electric current type |
| ---------------------------- | -------------------------------------------- | ------------- | --------------------- |
| HouseholdSocket              | household, hhs, schuko-simple                | 2.3           | AC                    |
| BlueHouseholdSocket          | bluehousehold, bhs, schuko-camping           | 3.6           | AC                    |
| Cee16ASocket                 | cee16                                        | 11            | AC                    |
| Cee32ASocket                 | cee32                                        | 22            | AC                    |
| Cee63ASocket                 | cee63                                        | 43            | AC                    |
| ChargingStationType1         | cst1, stationtype1, cstype1                  | 7.2           | AC                    |
| ChargingStationType2         | cst2, stationtype2, cstype2                  | 43            | AC                    |
| ChargingStationCcsComboType1 | csccs1, csccscombo1                          | 11            | DC                    |
| ChargingStationCcsComboType2 | csccs2, csccscombo2                          | 50            | DC                    |
| TeslaSuperChargerV1          | tesla1, teslav1, supercharger1, supercharger | 135           | DC                    |
| TeslaSuperChargerV2          | tesla2, teslav2, supercharger2               | 150           | DC                    |
| TeslaSuperChargerV3          | tesla3, teslav3, supercharger3               | 250           | DC                    |

### Custom Types

While the provided standard types should be suitable for most scenarios, providing an individual type for a specific
scenario might be necessary. To do so, a custom type can be provided instead of a common id. This custom type is tested
against the following regex {code}`(\w+\d*)\s*\(\s*(\d+\.?\d+)\s*\|\s*(AC|DC)\s*\)`, or more generally, the custom
type string has to be in the following syntax:

```
<Name>(<Apparent Power in kVA>|<AC|DC>) e.g. FastCharger(50|DC) or SlowCharger(2.5|AC)
```

Please note, that in accordance with {code}`edu.ie3.datamodel.models.StandardUnits` the apparent power is expected to
be in kVA!

### Limitations

- the available charging types are currently limited to only some common standard charging point types and not configurable
  via a type file or table. Nevertheless, providing custom types is possible using the syntax explained above.
  If there is additional need for a more granular type configuration via type file please contact us.
- each charging station can hold one or more charging points. If more than one charging point is available
  all attributes (e.g. {code}`sRated` or {code}`connectionType`) are considered to be equal for all connection
  points

(location-types)=

## Location types

Evcs location types describe the type of charging location of a charging station. Parsing of these types is case-insensitive
and underscores and minuses are ignored, that means "charginghubtown" is parsed as type {code}`CHARGING_HUB_TOWN`.

| type name            | public/private | description                    |
| -------------------- | -------------- | ------------------------------ |
| HOME                 | private        | Charging at home               |
| WORK                 | private        | Charging at work               |
| CUSTOMER_PARKING     | public         | Charging at store parking lots |
| STREET               | public         | Charging at street side        |
| CHARGING_HUB_TOWN    | public         | Charging at hub in town        |
| CHARGING_HUB_HIGHWAY | public         | Charging at hub out of town    |

## Caveats

Nothing - at least not known.
If you found something, please contact us!
