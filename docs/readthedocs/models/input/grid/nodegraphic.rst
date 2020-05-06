.. _node_graphic_model:

Schematic Node Graphic
----------------------
Schematic drawing information for a line model.

.. _node_graphic_attributes:

Attributes, Units and Hints
^^^^^^^^^^^^^^^^^^^^^^^^^^^
+--------------+------+----------------------------------------------------------------------+
| Attribute    | Unit | Hints                                                                |
+==============+======+======================================================================+
| uuid         | --   |                                                                      |
+--------------+------+----------------------------------------------------------------------+
| graphicLayer | --   | | Human readable identifier of the graphic layer to draw             |
|              |      | | this element on                                                    |
+--------------+------+----------------------------------------------------------------------+
| path         | --   | Line string of coordinates describing the drawing, e.g. for bus bars |
+--------------+------+----------------------------------------------------------------------+
| point        | --   | Alternative to line string, only drawing a point coordinate          |
+--------------+------+----------------------------------------------------------------------+
| node         | --   | Reference to the physical node model                                 |
+--------------+------+----------------------------------------------------------------------+

.. _node_graphic_caveats:

Caveats
^^^^^^^
Noting - at least not known.
If you found something, please contact us!
