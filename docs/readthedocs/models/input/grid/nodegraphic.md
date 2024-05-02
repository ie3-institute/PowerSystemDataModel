(node-graphic-model)=

# Schematic Node Graphic

Schematic drawing information for a node model.

## Attributes, Units and Remarks

```{eval-rst}
.. list-table::
   :widths: 33 33 33
   :header-rows: 1


   * - Attribute
     - Unit
     - Remarks

   * - uuid
     - --
     -

   * - graphicLayer
     - --
     - | Human readable identifier of the graphic layer to draw
       | this element on

   * - path
     - --
     - Line string of coordinates describing the drawing, e.g. for bus bars

   * - point
     - --
     - Alternative to line string, only drawing a point coordinate

   * - node
     - --
     - Reference to the physical node model

```

## Caveats

Nothing - at least not known.
If you found something, please contact us!
