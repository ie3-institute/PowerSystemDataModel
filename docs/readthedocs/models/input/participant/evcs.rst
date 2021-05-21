.. _evcs_model:

Electric Vehicle Charging Station
---------------------------------
Model of a charging station for electric vehicles. This model only covers the basic characteristics of a charging
station and has some limitations outlined below.

Model Definition
^^^^^^^^^^^^^^^^

Entity Model
""""""""""""

+------------------+---------+--------------------------------------------------------------------------------------+
| Attribute        | Unit    | Remarks                                                                              |
+==================+=========+======================================================================================+
| uuid             | --      |                                                                                      |
+------------------+---------+--------------------------------------------------------------------------------------+
| id               | --      | Human readable identifier                                                            |
+------------------+---------+--------------------------------------------------------------------------------------+
| operator         | --      |                                                                                      |
+------------------+---------+--------------------------------------------------------------------------------------+
| operationTime    | --      | Timely restriction of operation                                                      |
+------------------+---------+--------------------------------------------------------------------------------------+
| node             | --      |                                                                                      |
+------------------+---------+--------------------------------------------------------------------------------------+
| qCharacteristics | --      | :ref:`Reactive power characteristic<participant_general_q_characteristic>` to follow |
+------------------+---------+--------------------------------------------------------------------------------------+
| type             | --      | :ref:`Charging point type<evcs_point_types>` (valid for all installed points)        |
+------------------+---------+--------------------------------------------------------------------------------------+
| chargingPoints   | --      | no of installed charging points @ the specific station                               |
+------------------+---------+--------------------------------------------------------------------------------------+
| cosPhiRated      | --      | Rated power factor                                                                   |
+------------------+---------+--------------------------------------------------------------------------------------+

Type Model
""""""""""""
In contrast to other models, electric vehicle charging station types are not configured via separate type file or table,
but 'inline' of a charging station entry. This is justified by the fact, that the station type (in contrast to e.g.
the type of a wind energy converter) only consists of a few, more or less standardized parameters, that are (most of the
time) equal for all manufacturers. Hence, to simplify the type model handling, types are provided either by a string
literal of their id or by providing a custom one. See :ref:`Charging point types<evcs_point_types>` for details of on
available standard types and how to use custom types.

The actual model definition for charging point types looks as follows:

+------------------------+---------+--------------------------------------------------------------------------------+
| Attribute              | Unit    | Remarks                                                                        |
+========================+=========+================================================================================+
| id                     | --      | Human readable identifier                                                      |
+------------------------+---+-----+--------------------------------------------------------------------------------+
| sRated                 | kVA     | Rated apparent power                                                           |
+------------------------+---+-----+--------------------------------------------------------------------------------+
| electricCurrentType    | --      | Electric current type                                                          |
+------------------------+---+-----+--------------------------------------------------------------------------------+
|synonymousIds           | --      | Set of alternative human readable identifiers                                  |
+------------------------+---------+--------------------------------------------------------------------------------+

.. _evcs_point_types:

Charging Point Types
^^^^^^^^^^^^^^^^^^^^
Available Standard Types
""""""""""""""""""""""""
To simplify the application of electric vehicle charging stations, some common standard types are available out-of-the-box.
They can either by used code wise or directly from database or file input by referencing their id or one of their
synonymous ids. All standard types can be found in :code:`edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointTypeUtils`.

+-------------------------------+-----------------------------------------------+---------------+-----------------------+
| id                            | synonymous ids                                | sRated in kVA | electric current type |
+===============================+===============================================+===============+=======================+
| HouseholdSocket               | household, hhs, schuko-simple                 | 2.3           | AC                    |
+-------------------------------+-----------------------------------------------+---------------+-----------------------+
| BlueHouseholdSocket           | bluehousehold, bhs, schuko-camping            | 3.6           | AC                    |
+-------------------------------+-----------------------------------------------+---------------+-----------------------+
| Cee16ASocket                  | cee16                                         | 11            | AC                    |
+-------------------------------+-----------------------------------------------+---------------+-----------------------+
| Cee32ASocket                  | cee32                                         | 22            | AC                    |
+-------------------------------+-----------------------------------------------+---------------+-----------------------+
| Cee63ASocket                  | cee63                                         | 43            | AC                    |
+-------------------------------+-----------------------------------------------+---------------+-----------------------+
| ChargingStationType1          | cst1, stationtype1, cstype1                   | 7.2           | AC                    |
+-------------------------------+-----------------------------------------------+---------------+-----------------------+
| ChargingStationType2          | cst2, stationtype2, cstype2                   | 43            | AC                    |
+-------------------------------+-----------------------------------------------+---------------+-----------------------+
| ChargingStationCcsComboType1  | csccs1, csccscombo1                           | 11            | DC                    |
+-------------------------------+-----------------------------------------------+---------------+-----------------------+
| ChargingStationCcsComboType2  | csccs2, csccscombo2                           | 50            | DC                    |
+-------------------------------+-----------------------------------------------+---------------+-----------------------+
| TeslaSuperChargerV1           | tesla1, teslav1, supercharger1, supercharger  | 135           | DC                    |
+-------------------------------+-----------------------------------------------+---------------+-----------------------+
| TeslaSuperChargerV2           | tesla2, teslav2, supercharger2                | 150           | DC                    |
+-------------------------------+-----------------------------------------------+---------------+-----------------------+
| TeslaSuperChargerV3           | tesla3, teslav3, supercharger3                | 250           | DC                    |
+-------------------------------+-----------------------------------------------+---------------+-----------------------+


Custom Types
""""""""""""
While the provided standard types should be suitable for most scenarios, providing an individual type for a specific
scenario might be necessary. To do so, a custom type can be provided instead of a common id. This custom type is tested
against the following regex :code:`(\w+\d*)\s*\(\s*(\d+\.?\d+)\s*\|\s*(AC|DC)\s*\)`, or more generally, the custom
type string has to be in the following syntax::

    <Name>(<Apparent Power in kVA>|<AC|DC>) e.g. FastCharger(50|DC) or SlowCharger(2.5|AC)

Please note, that in accordance with :code:`edu.ie3.datamodel.models.StandardUnits` the apparent power is expected to
be in kVA!

Limitations
"""""""""""

- the available charging types are currently limited to only some common standard charging point types and not configurable
  via a type file or table. Nevertheless, providing custom types is possible using the syntax explained above.
  If there is additional need for a more granular type configuration via type file please contact us.
- each charging station can hold one or more charging points. If more than one charging point is available
  all attributes (e.g. :code:`sRated` or :code:`connectionType`) are considered to be equal for all connection
  points

Caveats
^^^^^^^
Nothing - at least not known.
If you found something, please contact us!
