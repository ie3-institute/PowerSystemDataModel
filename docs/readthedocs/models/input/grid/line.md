# Line

Representation of an AC line.

## Attributes, Units and Remarks

### Type Model

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

   * - r
     -  Ω / km
     - Phase resistance per length  

   * - x
     - Ω / km
     - Phase resistance per length

   * - g
     - µS / km
     - Phase-to-ground conductance per length

   * - b
     - µS / km
     - Phase-to-ground conductance per length

   * - iMax
     - A
     - Maximum permissible current
     
   * - vRated
     - kV
     - Rated voltage

```

A list with some standard line types can be found here: [Standard Line
Types](#standard-line-types)

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
     -

   * - nodeB
     - –
     -

   * - parallelDevices
     - –
     - overall amount of parallel lines to automatically construct
       (e.g. parallelDevices = 2 will build a total of two lines
       using the specified parameters)

   * - type
     - –
     -

   * - length
     - k m
     -

   * - geoPosition
     - –
     - Line string of geographical locations describing the
       position of the line

   * - olmCharacteristic
     - –
     - Characteristic of possible overhead line monitoring Can be
       given in the form of ``olm:{<List of Pairs>}``. The pairs
       are wind velocity in x and permissible loading in y.

```

## Standard Line Types

Following there are some standard line types with their source. A ``csv file`` containing the types listed below can be found
[here](https://github.com/ie3-institute/PowerSystemDataModel/tree/dev/input/StandardAssetTypes). This file can be used directly
for any simulation with ``simona``.
The lines which source is ``simBench`` are from [here](https://simbench.de/en/download/datasets/).


### Overhead Lines

Some standard overhead lines.

```{eval-rst}
.. list-table::
   :widths: 11 11 11 11 11 11 11 11 11
   :header-rows: 0
   
   
   * - uuid
     - b
     - g
     - iMax
     - id
     - r
     - vRated
     - x
     - source
     
   * - 91617ab8-3de2-4fba-be45-a54473ba09a9
     - 3.61283
     - 0.0
     - 1300.0
     - LineType_1
     - 0.08
     - 380.0
     - 0.32
     - simBench
     
   * - b3b231ae-a971-4432-80d7-4ce2f2a56a32
     - 3.22799
     - 0.0
     - 1950.0
     - LineType_4
     - 0.033333
     - 380.0
     - 0.333333
     - simBench
     
   * - 24595f91-8295-41f8-a3d8-c9418d860d9c
     - 1.076
     - 0.0
     - 650.0
     - LineType_6
     - 0.1
     - 380.0
     - 1.0
     - simBench
     
   * - f0fc57ec-aa5a-4484-b870-be70a5428cbd
     - 6.45597
     - 0.0
     - 3900.0
     - LineType_9
     - 0.016667
     - 380.0
     - 0.166667
     - simBench
     
   * - ba70d8e7-b082-49bc-8c45-3c10e1236c3e
     - 8.60796
     - 0.0
     - 5200.0
     - LineType_10
     - 0.0125
     - 380.0
     - 0.125
     - simBench
     
   * - veee8eeed-62c9-4345-aa5a-3743fe32007d
     - 12.9119
     - 0.0
     - 7800.0
     - LineType_11
     - 0.008333
     - 380.0
     - 0.083333
     - simBench
     
   * - d2b16935-dcd7-44d2-8623-cec4c703ccdc
     - 17.2159
     - 0.0
     - 10400.0
     - LineType_12
     - 0.00625
     - 380.0
     - 0.0625
     - simBench
     
   * - a490c96e-6e90-485a-b0d7-adeb81fa09cd
     - 4.30398
     - 0.0
     - 2600.0
     - LineType_2
     - 0.025
     - 220.0
     - 0.25
     - simBench
     
   * - 5272bcbc-7d0e-4759-85fa-27943fd8d19c
     - 2.15199
     - 0.0
     - 1300.0
     - LineType_3
     - 0.05
     - 220.0
     - 0.5
     - simBench
     
   * - dd0bac07-de8d-4608-af36-b8ff2819f55a
     - 7.22566
     - 0.0
     - 2600.0
     - LineType_5
     - 0.04
     - 220.0
     - 0.16
     - simBench
     
   * - 64c1dcb5-57a5-4f35-b2bf-9ae4e6cc4943
     - 1.80642
     - 0.0
     - 650.0
     - LineType_7
     - 0.16
     - 220.0
     - 0.64
     - simBench
     
   * - bdc83a85-c796-4bcb-8b79-8988dc2804f8
     - 5.41925
     - 0.0
     - 1950.0
     - LineType_8
     - 0.053333
     - 220.0
     - 0.213333
     - simBench
     
   * - 3d75fb6b-f0be-4451-ab4c-7f00c0ebd619
     - 2.8274
     - 0.0
     - 680.0
     - Al/St_265/35
     - 0.1095
     - 110.0
     - 0.296
     - simBench
     
   * - 5b542a50-b0c2-4497-ba90-b2b31aafaa0b
     - 2.87456
     - 0.0
     - 170.0
     - 34-AL1/6-ST1A 20.0
     - 0.8342
     - 20.0
     - 0.382
     - simBench
     
   * - d594cd67-4459-44bc-9594-db710372db71
     - 2.98451
     - 0.0
     - 210.0
     - 48-AL1/8-ST1A 20.0
     - 0.5939
     - 20.0
     - 0.372
     - simBench
     
   * - 305e60ad-cfd2-4127-9d83-8d9b21942d93
     - 3.04734
     - 0.0
     - 290.0
     - 70-AL1/11-ST1A 20.0
     - 0.4132
     - 20.0
     - 0.36
     - simBench   
```



### Cables

Some standard cables.

```{eval-rst}
.. list-table::
   :widths: 11 11 11 11 11 11 11 11 11
   :header-rows: 0
   
   * - uuid
     - b
     - g
     - iMax
     - id
     - r
     - vRated
     - x
     - source

   * - cc59abd4-770b-45d2-98c8-919c91f1ca4b
     - 58.7478
     - 0.0
     - 652.0
     - 1x630_RM/50
     - 0.122
     - 110.0
     - 0.122522
     - simBench

   * - 82ea1b98-2b21-48bd-841a-8d17d8ac20c9
     - 59.3761
     - 0.0
     - 158.0
     - NA2XS2Y 1x50 RM/25 12/20 kV
     - 0.64
     - 20.0
     - 0.145
     - simBench

   * - 4adef9e6-5e40-416d-8bd2-b6768d156c54
     - 59.6903
     - 0.0
     - 220.0
     - NA2XS2Y 1x70 RM/25 12/20 kV
     - 0.443
     - 20.0
     - 0.132
     - simBench

   * - d5c03484-59c2-44d5-a2ee-63a5a0d623b4
     - 67.8584
     - 0.0
     - 252.0
     - NA2XS2Y 1x95 RM/25 12/20 kV
     - 0.313
     - 20.0
     - 0.132
     - simBench

   * - 9c13909d-1dd1-4e2d-980b-55345bdf0fd0
     - 72.2566
     - 0.0
     - 283.0
     - NA2XS2Y 1x120 RM/25 12/20 kV
     - 0.253
     - 20.0
     - 0.119
     - simBench

   * - 36243493-eb31-4e81-bd13-b54ef59c4cbe
     - 78.5398
     - 0.0
     - 319.0
     - NA2XS2Y 1x150 RM/25 12/20 kV
     - 0.206
     - 20.0
     - 0.116
     - simBench

   * - 437689f8-366d-4b04-b42d-d7a754db074b
     - 85.7655
     - 0.0
     - 362.0
     - NA2XS2Y 1x185 RM/25 12/20 kV
     - 0.161
     - 20.0
     - 0.117
     - simBench

   * - b459115d-d4eb-47d4-b7ec-625339ee0dcc
     - 95.5044
     - 0.0
     - 421.0
     - NA2XS2Y 1x240 RM/25 12/20 kV
     - 0.122
     - 20.0
     - 0.112
     - simBench

   * - 9aed5818-c037-4033-8d15-806c62d70b8f
     - 113.097
     - 0.0
     - 315.0
     - NA2XS2Y 1x150 RM/25 6/10 kV
     - 0.206
     - 10.0
     - 0.11
     - simBench

   * - 60d37bc7-157a-4c32-b1b5-e74c10d70531
     - 127.549
     - 0.0
     - 358.0
     - NA2XS2Y 1x185 RM/25 6/10 kV
     - 0.161
     - 10.0
     - 0.11
     - simBench

   * - a3ced617-2ffd-4593-b8e9-bcad9a521aab
     - 143.257
     - 0.0
     - 416.0
     - NA2XS2Y 1x240 RM/25 6/10 kV
     - 0.122
     - 10.0
     - 0.105
     - simBench

   * - f0484bb6-9d0d-4d13-bfbe-b83783b8352a
     - 150.796
     - 0.0
     - 471.0
     - NA2XS2Y 1x300 RM/25 6/10 kV
     - 0.1
     - 10.0
     - 0.0974
     - simBench

   * - 6b223bc3-69e2-4eb8-a2c0-76be1cd2c998
     - 169.646
     - 0.0
     - 535.0
     - NA2XS2Y 1x400 RM/25 6/10 kV
     - 0.078
     - 10.0
     - 0.0942
     - simBench

   * - 65181464-230a-487b-978f-81e406e9eb22
     - 260.752
     - 0.0
     - 270.0
     - NAYY 4x150SE 0.6/1kV
     - 0.2067
     - 0.4
     - 0.0804248
     - simBench

   * - 1200d9eb-6d10-47f3-8543-abea43b128d3
     - 273.319
     - 0.0
     - 357.0
     - NAYY 4x240SE 0.6/1kV
     - 0.1267
     - 0.4
     - 0.0797965
     -simBench
```


## Caveats

Nothing - at least not known.
If you found something, please contact us!
