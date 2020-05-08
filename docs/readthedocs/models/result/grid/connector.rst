.. _connector_model:

connector
---------
Representation of all kinds of connectors.

.. _connector_attributes:

Attributes, Units and Remarks
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

+---------------+----------------+----------------------------------------------------------+
| Attribute     | Unit           | Remarks                                                  |
+===============+================+==========================================================+
| timeStamp     | ZonedDateTime  |   date and time for the produced result                  |
+---------------+----------------+----------------------------------------------------------+
| inputModel    | --             |   uuid for the input model                               |
+---------------+----------------+----------------------------------------------------------+
| iAMag         | ampere         |   A stands for sending node                              |
+---------------+----------------+----------------------------------------------------------+
| iAAng         | degree         |                                                          |
+---------------+----------------+----------------------------------------------------------+
| iBMag         | ampere         |   B stands for receiving node                            |
+---------------+----------------+----------------------------------------------------------+
| iBAng         | degree         |                                                          |
+---------------+----------------+----------------------------------------------------------+

.. _connector_caveats:

Caveats
^^^^^^^
Groups all available connectors i.e. lines, switches and transformers
