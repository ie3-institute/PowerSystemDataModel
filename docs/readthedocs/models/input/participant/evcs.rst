.. _evcs_model:

Electric Vehicle Charging Station
--------------------------------
Model of a charging station for electric vehicles. This model only covers the basic characteristics of a charging
station and has some limitations outlined below.

Attributes, Units and Remarks
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

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
| cosphiRated      | --      | Rated power factor                                                                   |
+------------------+---------+--------------------------------------------------------------------------------------+

.. _evcs_point_types:

Available Standard Types
""""""""""""""""""""""""

Custom Types
""""""""""""

Limitations
"""""""""""

- the available charging types are currently limited to only some common standard charging point types and not configurable
  via a type file. Although providing custom types is possible using the syntax explained above. If there is additional
  need for a more granular type configuration via type file please contact us.
- each charging station can hold one or more charging points. If more than one charging point is available
  all attributes (e.g. :code:`sRated` or :code:`connectionType`) are considered to be equal for all connection
  points

Caveats
^^^^^^^
Nothing - at least not known.
If you found something, please contact us!