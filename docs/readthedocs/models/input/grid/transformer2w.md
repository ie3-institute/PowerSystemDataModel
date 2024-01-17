# Two Winding Transformer

Model of a two winding transformer.
It is assumed, that node A is the node with higher voltage.

## Attributes, Units and Remarks

### Type Model

All impedances and admittances are given with respect to the higher voltage side.
As obvious, the parameter can be used in T- as in 𝜋-equivalent circuit representations.

```{eval-rst}
.. list-table::
   :widths: 33 33 33
   :header-rows: 0

   * - Attribute
     - Un it
     - Remarks

   * - id
     -
     - Human readable identifier

   * - rSc
     - Ω
     - Short circuit resistance

   * - xSc
     - Ω
     - Short circuit reactance

   * - gM
     - nS
     - No load conductance

   * - bM
     - nS
     - No load susceptance

   * - sRated
     - k VA
     - Rated apparent power

   * - vRatedA
     - kV
     - Rated voltage at higher voltage terminal

   * - vRatedB
     - kV
     - Rated voltage at lower voltage terminal

   * - dV
     - %
     - Voltage magnitude increase per tap position

   * - dPhi
     - °
     - Voltage angle increase per tap position

   * - tapSide
     -
     - true, if tap changer is installed on lower voltage
       side

   * - tapNeutr
     -
     - Neutral tap position

   * - tapMin
     -
     - Minimum tap position

   * - tapMax
     -
     - Maximum tap position
```

A list with some standard transformer types can be found here: `Standard Two Winding Transformer Types`_


### Entity Model

```{eval-rst}
.. list-table::
   :widths: 33 33 33
   :header-rows: 0

   * - Attribute
     - U n i t
     - Remarks

   * - uuid
     - –
     -

   * - id
     - –
     - Human readable identifier

   * - operator
     - –
     -

   * - operationTime
     - –
     - Timely restriction of operation

   * - nodeA
     - –
     - Higher voltage node

   * - nodeB
     - –
     - Lower voltage node

   * - parallelDevices
     - –
     - overall amount of parallel transformers to automatically
         construct (e.g. parallelDevices = 2 will build a total of
         two transformers using the specified parameters)

   * - type
     - –
     -

   * - tapPos
     - –
     - Current position of the tap changer

   * - autoTap
     - –
     - true, if there is a tap regulation apparent and active
```

## Standard Two Winding Transformer Types


Following there are some standard two winding transformer types with their source. A ``csv file`` containing the types listed
below can be found `here <https://github.com/ie3-institute/PowerSystemDataModel/tree/dev/input/StandardAssetTypes>`_. This
file can be used directly for any simulation with ``simona``.
The transformers which source is ``simBench`` are from `here <https://simbench.de/en/download/datasets/>`_.

```{eval-rst}
.. list-table::
   :widths: 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6
   :header-rows: 0
   * - uuid
     - bM
     - dPhi
     - dV
     - gM
     - id
     - rSc
     - sRated
     - tapMax
     - tapMin
     - tapNeutr
     - tapSide
     - vRatedA
     - vRatedB
     - xSc
     - source
   * - 5a890aae-b9c9-4ebf-8a49-8850ae9df402
     - 219.43184927638458
     - 0.0
     - 1.0
     - 1731.3019390581715
     - Typ_x_380/220
     - 0.6016666666666666
     - 600000.0
     - 16
     - -16
     - 0
     - false
     - 380.0
     - 220.0
     - 44.51926783240413
     - simBench
   * - 03159c0d-126e-47cc-9871-066870df3a3f
     - 1193.4686938790917
     - 0.0
     - 1.0
     - 831.0249307479223
     - 350MVA_380/110
     - 1.0608979591836734
     - 350000.0
     - 16
     - -16
     - 0
     - false
     - 380.0
     - 110.0
     - 90.75951402093402
     - simBench
   * - 7cb289cb-e6af-4470-9c68-e5a91978a5e7
     - 2013.800484464662
     - 0.0
     - 1.0
     - 1446.280991735537
     - 300MVA_220/110
     - 0.20704444444444442
     - 300000.0
     - 16
     - -16
     - 0
     - false
     - 220.0
     - 110.0
     - 19.358892855688435
     - simBench
   * - 73644bc6-78cf-4882-9837-e6508cab092d
     - 867.7685950413226
     - 0.0
     - 1.5
     - 1157.0247933884295
     - 25 MVA 110/20 kV YNd5
     - 1.9843999999999997
     - 25000.0
     - 9
     - -9
     - 0
     - false
     - 110.0
     - 20.0
     - 58.04608993412045
     - simBench
   * - 6935ae26-374a-4c24-aeee-6d5760d6ddf3
     - 720.4791642215993
     - 0.0
     - 1.5
     - 1487.603305785124
     - 40 MVA 110/20 kV YNd5
     - 1.0285
     - 40000.0
     - 9
     - -9
     - 0
     - false
     - 110.0
     - 20.0
     - 48.994205909984906
     - simBench
   * - b49db20f-b8b5-4265-8318-f669b9d121e9
     - 1015.6886939330394
     - 0.0
     - 1.5
     - 1818.181818181818
     - 63 MVA 110/10 kV YNd5
     - 0.6146031746031745
     - 63000.0
     - 9
     - -9
     - 0
     - false
     - 110.0
     - 10.0
     - 34.56596500037509
     - simBench
   * - 0843b836-cee4-4a8c-81a4-098400fe91cf
     - 24.495101551166183
     - 0.0
     - 2.5
     - 2999.9999999999995
     - 0.4 MVA 20/0.4 kV Dyn5 ASEA
     - 11.999999999999998
     - 400.0
     - 2
     - -2
     - 0
     - false
     - 20.0
     - 0.4
     - 58.787753826796276
     - simBench
   * - a8f3aeea-ef4d-4f3c-bb07-09a0a86766c1
     - 9.591746452043322
     - 0.0
     - 2.5
     - 1149.9999999999998
     - 0.16 MVA 20/0.4 kV DOTE 160/20  SGB
     - 36.71874999999999
     - 160.0
     - 2
     - -2
     - 0
     - false
     - 20.0
     - 0.4
     - 93.01469452961452
     - simBench
   * - 0644c120-a247-425f-bbe4-31b153f7f440
     - 16.583241729259253
     - 0.0
     - 2.5
     - 2199.9999999999995
     - 0.25 MVA 20/0.4 kV Dyn5 ASEA
     - 21.119999999999997
     - 250.0
     - 2
     - -2
     - 0
     - false
     - 20.0
     - 0.4
     - 93.6479876986153
     - simBench
   * - bdf22ee4-deba-41f4-a187-ae00638a6880
     - 36.47380569074435
     - 0.0
```

## Caveats

Nothing - at least not known.
If you found something, please contact us!
