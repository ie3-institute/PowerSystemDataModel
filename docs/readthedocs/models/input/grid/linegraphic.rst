.. _line_graphic_model:

Schematic Line Graphic
----------------------
Whoops!
Seems, you found a construction site...
Sorry, that we cannot provide you with this information at the moment.
But we are very happy to help you, please just contact us!

.. _line_graphic_attributes:

Attributes, Units and Hints
^^^^^^^^^^^^^^^^^^^^^^^^^^^
+-----------+------+-------+
| Attribute | Unit | Hints |
+-----------+------+-------+
| uuid      | --   |       |
+-----------+------+-------+

.. _line_graphic_example:

Application example
^^^^^^^^^^^^^^^^^^^
.. code-block:: java
  :linenos:

  NodeInput node = new NodeInput(
      UUID.fromString("4ca90220-74c2-4369-9afa-a18bf068840d"),
      "node_a",
      profBroccoli,
      defaultOperationTime,
      Quantities.getQuantity(1d, PU),
      true,
      geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [7.411111, 51.492528] }") as Point,
      GermanVoltageLevelUtils.EHV_380KV,
      1
    )

.. _line_graphic_caveats:

Caveats
^^^^^^^
Noting - at least not known.
If you found something, please contact us!
