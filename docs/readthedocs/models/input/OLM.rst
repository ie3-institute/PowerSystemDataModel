.. _olm_model:

overhead line monitoring
----
Representation of an olm type according to IEEE Standard 2012.

Attributes, Units and Remarks
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Type Model
""""""""""

+-------------------+---------+---------------------------------------------+
| Attribute         | Unit    | Remarks                                     |
+===================+=========+=============================================+
| uuid              | --      |                                             |
+-------------------+---------+---------------------------------------------+
| id                | --      | Human readable identifier                   |
+-------------------+---------+---------------------------------------------+
| temperatureMax    | °C      | Max. line temperature                       |
+-------------------+---------+---------------------------------------------+
| alphaLine         | 1/K     | Coefficient of thermal expansion            |
+-------------------+---------+---------------------------------------------+
| layers            | --      | Number of layers per line                   |
+-------------------+---------+---------------------------------------------+
| conductorArea     | mm^2    | Cross section                               |
+-------------------+---------+---------------------------------------------+
| conductorDiameter | m       | Conductor diameter                          |
+-------------------+---------+---------------------------------------------+
| rDC20             | Ω / km  | Specific DC resistance at 20°C              |
+-------------------+---------+---------------------------------------------+
| bundles           | --      | Overall number of bundles                   |
+-------------------+---------+---------------------------------------------+
| emissivity        | --      | Emissivity according to IEEE Standard 2012  |
+-------------------+---------+---------------------------------------------+
| alphaSolar        | --      | Solar absorptivity                          |
+-------------------+---------+---------------------------------------------+




Caveats
^^^^^^^
Nothing - at least not known.
If you found something, please contact us!