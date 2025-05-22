(pv-model)=

# Photovoltaic Power Plant

Detailed model of a photovoltaic power plant.

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1


   * - Attribute
     - Unit
     - Remarks

   * - uuid
     -
     -

   * - id
     -
     - Human readable identifier

   * - operator
     -
     -

   * - operationTime
     -
     - Timely restriction of operation

   * - node
     -
     -

   * - qCharacteristics
     -
     - [Reactive power characteristic](#participant-general-q-characteristic) to follow

   * - albedo
     -
     - [Albedo](https://en.wikipedia.org/wiki/Albedo) of the plant's surrounding

   * - azimuth
     - °
     - Inclination in a compass direction
       South = 0°, West = 90°, East = -90°

   * - etaConv
     - %
     - Efficiency of the assets inverter

   * - elevationAngle
     - °
     - Tilted inclination from horizontal [0°, 90°]

   * - kG
     -
     - Generator correction factor merging technical influences

   * - kT
     -
     - Temperature correction factor merging thermal influences

   * - marketReaction
     -
     - Whether to adapt output based on (volatile)
       market price or not

   * - sRated
     - kVA
     - Rated apparent power

   * - cosPhiRated
     -
     - Rated power factor

   * - controllingEm
     -
     - UUID reference to an [Energy Management Unit](#em_model) that is controlling
       this system participant. Field can be empty or missing, if this participant
       is not controlled.

```

## Caveats

Nothing - at least not known.
If you found something, please contact us!
