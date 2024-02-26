(pv-model)=

# Photovoltaic Power Plant

Detailed model of a photovoltaic power plant.

```{eval-rst}
.. list-table::
   :widths: 33 33 33
   :header-rows: 0


   * - Attribute
     - Unit
     - Remarks

   * - uuid
     - --
     -

   * - id
     - --
     - Human readable identifier

   * - operator
     - --
     -

   * - operationTime
     - --
     - Timely restriction of operation

   * - node
     - --
     -

   * - qCharacteristics
     - --
     - :ref:`Reactive power characteristic<participant-general-q-characteristic>` to follow

   * - albedo
     - --
     - `Albedo <https://en.wikipedia.org/wiki/Albedo>`_ of the plant's surrounding

   * - azimuth
     - °
     - | Inclination in a compass direction
       | South = 0°, West = 90°, East = -90°

   * - etaConv
     - %
     - Efficiency of the assets inverter

   * - elevationAngle
     - °
     - Tilted inclination from horizontal [0°, 90°]

   * - kG
     - --
     - Generator correction factor merging technical influences

   * - kT
     - --
     - Temperature correction factor merging thermal influences

   * - marketReaction
     - --
     - | Whether to adapt output based on (volatile)
       | market price or not

   * - sRated
     - kVA
     - Rated apparent power

   * - cosPhiRated
     - --
     - Rated power factor

   * - em
     - --
     - | UUID reference to an :ref:`Energy Management Unit<em_model>` that is controlling
       | this system participant. Field can be empty or missing, if this participant
       | is not controlled.

```

## Caveats

Nothing - at least not known.
If you found something, please contact us!
