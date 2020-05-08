.. _transformer3W_model:

Transformer3W
-------------
Representation of three winding transformers.

.. _transformer3W_attributes:

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
| iCMag         | ampere         |   B stands for receiving node                            |
+---------------+----------------+----------------------------------------------------------+
| iCAng         | degree         |                                                          |
+---------------+----------------+----------------------------------------------------------+
| tapPos        | --             |                                                          |
+---------------+----------------+----------------------------------------------------------+

.. _transformer3W_caveats:

Caveats
^^^^^^^
Assumption: Node A is the node at highest voltage and Node B is at intermediate voltage.
For model specifications please check corresponding input model documentation.
