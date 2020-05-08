.. _node_model:

Node
----
Representation of an electrical node, with no further distinction into bus bar, auxiliary node or others.

.. _node_attributes:

Attributes, Units and Hints
^^^^^^^^^^^^^^^^^^^^^^^^^^^
+---------------+----------------+----------------------------------------------------------+
| Attribute     | Unit           | Remarks                                                  |
+===============+================+==========================================================+
| timeStamp     | ZonedDateTime  |   date and time for the produced result                  |
+---------------+----------------+----------------------------------------------------------+
| uuid          | --             |                                                          |
+---------------+----------------+----------------------------------------------------------+
| vMag          | p.u.           |                                                          |
+---------------+----------------+----------------------------------------------------------+
| vAng          | degree         |                                                          |
+---------------+----------------+----------------------------------------------------------+

.. _node_caveats:

Caveats
^^^^^^^
System participants, that need to have geographical locations, inherit the position from the node.
If the overall location does not play a too big role, you are able to use the default location with
:code:`NodeInput#DEFAULT_GEO_POSITION` being located on TU Dortmund university's campus (`See on OpenStreetMaps <https://www.openstreetmap.org/search?query=51.4843281%2C%207.4116482#map=15/51.4843/7.4117>`_).
