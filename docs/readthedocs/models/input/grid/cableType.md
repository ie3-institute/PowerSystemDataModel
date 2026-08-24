# Cable Type

Representation of a cable type.

## Attributes, Units and Remarks

### Type Model

Type model of a cable. 

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1

   * - Attribute
     - Unit
     - Remarks

   * - uuid
     - –
     - Identifier

   * - id
     - –
     - Human readable identifier
     
   * - core number
     - –
     - Number of conductor cores in the cable

   * - conductor
     - ConductorInput
     - Layer model that represents the attributes and geometry of the conductor.  

   * - isolation
     - List of LayerInput
     - List of insulation layers (from inner to outer)

   * - screen
     - Optional ScreenLayerInput
     - Optional cable screen layer

   * - filler
     - List of LayerInput
     - List of filler layers (from inner to outer)

   * - armor 
     - List of LayerInput
     - List of armor layers (from inner to outer)
     
   * - jack
     - List of LayerInput
     - List of outer sheath or jack layers (from inner to outer)
     
   * - limit temperature
     - °C
     - Maximum permissible operating temperature
        
   * - frequency
     - Hz
     - Rated frequency of the system     

   * - skin effect coefficient
     - -
     - Skin effect coefficient

   * - proximity effect coefficient
     - 
     - Proximity effect coefficient
   
   * - electrical capacitance
     - F/m
     - Capacitance per unit length
   
   * - Dielectric loss factor tanDelta
     - -
     - Dielectric loss factor tan(δ)
   
   * - circulatingLossFactor
     - -
     - Circulating loss factor 
     
   * - eddyCurrentLossFactor
     - -
     - Eddy current loss factor
```

A list with some standard cable types can be found here: [Standard Cable Type Parameter](#standard-cable-type-parameter)

### Cable Layers

Cables are modeled as a series of concentric layers. These layers—which include insulation, filler, armor, and outer sheaths—are defined using the LayerInput class. Each layer tracks its physical dimensions and thermal properties to support precise electrical and thermal simulations.

#### LayerInput Attributes

The following table details the attributes required to define a single cable 

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1

* - Layer Attribute
  - Type
  - Description
    
* - name
  - String
  - Designation of the layer (e.g., "Main insulation")
    
* - material
  - CableMaterial
  - Material of the layer
      
* - innerDiameter
  - Length
  - Inner diameter of the layer
    
* - outerDiameter
  - Length
  - Outer diameter of the layer
    
* - thermalResistivity
  - (K·m/W) 
  - Thermal resistivity of the material
    
* - thermalCapacitance
  - J/(m³·K)
  - Thermal capacitance of the material
    
* - area
  - Optional Area
  - Real cross-sectional area. If none, area will be calculated from geometry.
```

Different cable materials and their thermal and electrical parameter are also given as described in [cableMaterial](#cable-material)

## Standard Cable Type Parameter

Following there are some standard cable types with their source.
A ``csv file`` containing the types listed below can be found [here](https://github.com/ie3-institute/PowerSystemDataModel/tree/dev/input/StandardAssetTypes).
This file can be used directly for any simulation with ``simona``.

### Cables

Some standard cables type parameter and geometries.

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1
   
   
* - uuid
  - id
  - core_number
  - conductor
  - isolation
  - screen
  - filler
  - armor
  - jack
  - limit_temperature
  - frequency
  - skin_effect_coefficient
  - proximity_effect_coefficient
  - electrical_capacitance
  - tan_delta
  - circulating_loss_factor
  - eddy_current_loss_factor
  - source

* - b8152c3f-d12f-4857-9746-a30aef6aee08
  - CigreT880_33kVLandCable
  - 1
  - "{""name"":""conductor"",""material"":""COPPER"",""crossSection"":""240.0"",""diameter"":""18.4"",""thermalResistivity"":""0.0026"",""thermalCapacitance"":""3.4e6"",""area"":""240.0"",""isCompacted"":false}"
  - "[{""name"":""conductorScreen"",""material"":""SEMI_COND_SCREEN"",""innerDiameter"":""18.4"",""outerDiameter"":""19.4"",""thermalResistivity"":""4.0"",""thermalCapacitance"":""2.0e6"",""area"":null},{""name"":""insulation"",""material"":""XLPE"",""innerDiameter"":""19.4"",""outerDiameter"":""34.8"",""thermalResistivity"":""3.5"",""thermalCapacitance"":""2.4e6"",""area"":null},{""name"":""insulationScreen"",""material"":""SEMI_COND_SCREEN"",""innerDiameter"":""34.8"",""outerDiameter"":""35.8"",""thermalResistivity"":""4.0"",""thermalCapacitance"":""2.0e6"",""area"":null},{""name"":""screenTape"",""material"":""SC_TAPE"",""innerDiameter"":""35.8"",""outerDiameter"":""36.8"",""thermalResistivity"":""0.01"",""thermalCapacitance"":""3.0e6"",""area"":null}]",
  - "{""name"":""screen"",""material"":""COPPER"",""innerDiameter"":""36.8"",""outerDiameter"":""38.6"",""thermalResistivity"":""0.0026"",""thermalCapacitance"":""3.4e6"",""area"":""35.62566"",""wiresNumber"":56,""wireDiameter"":""0.9"",""electricalResistivity"":""1.7241e-8""}"
  - -
  - -
  - "[{""name"":""jackTape"",""material"":""SC_TAPE"",""innerDiameter"":""38.6"",""outerDiameter"":""39.2"",""thermalResistivity"":""0.01"",""thermalCapacitance"":""3.0e6"",""area"":null},{""name"":""jack"",""material"":""XLPE"",""innerDiameter"":""39.2"",""outerDiameter"":""43.6"",""thermalResistivity"":""3.5"",""thermalCapacitance"":""2.4e6"",""area"":null},{""name"":""outerCover"",""material"":""SEMI_COND_SCREEN"",""innerDiameter"":""43.6"",""outerDiameter"":""44.0"",""thermalResistivity"":""4.0"",""thermalCapacitance"":""2.0e6"",""area"":null}]"
  - 90.0
  - 50.0
  - 1.0
  - 1.0
  - 0.000000000237683304
  - 0.004
  - 0.0435122656
  - 0.0
  - CIGRE TB880 B1.56 Power cable rating examples for calculation tool verification

```


## Caveats

Nothing - at least not known.
If you found something, please contact us!
