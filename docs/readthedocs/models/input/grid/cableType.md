# Cable type

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

   * - screenLayer
     - Optional ScreenLayer
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

A list with some standard line types can be found here: [Standard Cable Type Parameter](#standard-cable-type-parameter)

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

//FIXME

Following there are some standard line types with their source. To retrieve the data call the method `TypeSource.getStandardLineTypes()`.
A ``csv file`` containing the types listed below can be found [here](https://github.com/ie3-institute/PowerSystemDataModel/tree/dev/input/StandardAssetTypes).
This file can be used directly for any simulation with ``simona``.
The lines which source is ``simBench`` are from [here](https://simbench.de/en/download/datasets/).


### Cables
//FIMXE
Some standard cables type parameter and geometries.

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1
   
   
   * - uuid
     - b [µS / km]
     - g [µS / km]
     - iMax [A]
     - id
     - r [Ω / km]
     - vRated [kV]
     - x [Ω / km]
     - source

```


## Caveats

Nothing - at least not known.
If you found something, please contact us!
