.. _node_model:

Node
----
Representation of an electrical node, with no further distinction into bus bar, auxiliary node or others.

Attributes, Units and Remarks
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
+---------------+------+--------------------------------------------------------------------+
| Attribute     | Unit | Remarks                                                            |
+===============+======+====================================================================+
| uuid          | --   |                                                                    |
+---------------+------+--------------------------------------------------------------------+
| id            | --   | Human readable identifier                                          |
+---------------+------+--------------------------------------------------------------------+
| operator      | --   |                                                                    |
+---------------+------+--------------------------------------------------------------------+
| operationTime | --   | Timely restriction of operation                                    |
+---------------+------+--------------------------------------------------------------------+
| vTarget       | p.u. | Target voltage magnitude to be used by voltage regulation entities |
+---------------+------+--------------------------------------------------------------------+
| slack         | --   | | Boolean indicator, if this nodes serves as a slack node in power |
|               |      | | flow calculation                                                 |
+---------------+------+--------------------------------------------------------------------+
| geoPosition   | --   | Geographical location                                              |
+---------------+------+--------------------------------------------------------------------+
| voltLvl       | --   | Information of the voltage level (id and nominal voltage)          |
+---------------+------+--------------------------------------------------------------------+
| subnet        | --   | Sub grid number                                                    |
+---------------+------+--------------------------------------------------------------------+

Caveats
^^^^^^^
System participants, that need to have geographical locations, inherit the position from the node.
If the overall location does not play a big role, you are able to use the default location with
:code:`NodeInput#DEFAULT_GEO_POSITION` being located on TU Dortmund university's campus (`See on OpenStreetMaps <https://www.openstreetmap.org/search?query=51.4843281%2C%207.4116482#map=15/51.4843/7.4117>`_).
