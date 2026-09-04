(cable-material)=
# Cable Material

## Overview

The `CableMaterial` enum represents various materials used in electrical cable construction, providing their default thermal and electrical properties. This is primarily used for physical modeling and simulation of distribution grids, allowing for accurate calculations of thermal states, ampacity, and electrical losses.

## Properties

### Thermal Properties

Retrieved via `getThermalProperties()`. Returns a `ThermalProperties` container providing Thermal Resistivity ($\mathrm{K}\cdot\mathrm{m}/\mathrm{W}$) and Thermal Capacitance ($\mathrm{J}/(\mathrm{m}^3\cdot\mathrm{K})$).

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1
   
  * - Material
    - Thermal Resistivity
      [$\mathrm{K}\cdot\mathrm{m}/\mathrm{W}$]
    - Thermal Capacitance
      [$\mathrm{J}/(\mathrm{m}^3\cdot\mathrm{K})$]
    - source 
    - Notes
  
  * - `Copper`
    - 1/384 
    - 3,449,600.0
    - {cite:cts}`wiki:thermal_conductivity_resistivity`, {cite:cts}`wiki:Copper` 
    - $c = 385\ \mathrm{J/(kg\cdot K)},\ \rho = 8.96\ \mathrm{g/cm^3}$
      $\Rightarrow\ 3{,}449{,}600\ \mathrm{J/(m^3\cdot K)}$
    
  * - `Aluminium`
    - 1/237
    - 2,420,913.3 
    - {cite:cts}`wiki:thermal_conductivity_resistivity`, {cite:cts}`wiki:Aluminium` 
    - $c = 897\ \mathrm{J/(kg\cdot K)},\ \rho = 2.6989\ \mathrm{g/cm^3}$
      $\Rightarrow\ 2{,}420{,}913.3\ \mathrm{J/(m^3\cdot K)}$
    
  * - `XLPE (Cross-linked polyethylene)`
    - 3.5
    - 2.4
    - {cite:cts}`andersRatingElectricPower1997` p. 400
    - -
    
  * - `PE (Polyethylene)`
    - 3.5
    - 2.4
    - {cite:cts}`andersRatingElectricPower1997` p. 400
    - - 
    
  * - `PVC (Polyvinyl chloride)`
    - 5.0
    - 1.7
    - {cite:cts}`andersRatingElectricPower1997` p. 400
    - -
    
  * - `Semi-Conductive Screen`
    - 2.5
    - 2.4
    - Th. Res.: {cite:cts}`CIGRE_TB880_2022` p. 28; Th. Capa.: Same as adjacent dielectric material see {cite:cts}`andersRatingElectricPower1997` p. 400
    - -

  * - `SC-Tape (Screen Tape)`
    - 6.0
    - 2.4
    - Th. Res.: {cite:cts}`CIGRE_TB880_2022` p. 28; Th. Capa.: Same as adjacent dielectric material see {cite:cts}`andersRatingElectricPower1997` p. 400
    - -
    
  * - `Lead`
    - 1/35
    - 1,463,892.0
    - Th. Res.: {cite:cts}`wiki:thermal_conductivities`; Th. Capa.: {cite:cts}`wiki:specific_heat_capacities` 
    - $c = 129\ \mathrm{J/(kg\cdot K)},\ \rho = 11.348\ \mathrm{g/cm^3}$
      $\Rightarrow\ 1{,}463{,}892.0\ \mathrm{J/(m^3\cdot K)}$
    
  * - `Steel`
    - 1/45
    - 3,756,000.0
    - Th. Res.: {cite:cts}`wiki:thermal_conductivity_resistivity`; Th. Capa.: {cite:cts}`wiki:specific_heat_capacities`
    - - 
    
  * - `Polypropylene`
    - 6.0
    - 2.0
    - Th. Res.: {cite:cts}`CIGRE_TB880_2022` p. 28; Th. Capa.: Assumed to be close to Paper-polypropylene-paper (PPL) in {cite:cts}`andersRatingElectricPower1997` p. 400
    - - 
```

**Note:** Metals inherently define their thermal resistivity as the inverse of their thermal conductivity $\lambda$ (e.g., $\lambda_{Copper} = 384 \, W/(m \cdot K)$).

### Electrical Properties

Electrical parameters define the conductive aspects of the materials, heavily utilized for power flow and loss calculations.

* **Electrical Resistivity:** Retrieved via `getElectricalResistivity()` (at standard reference temperature).
* **Temperature Coefficient:** Retrieved via `getElectricalResistivityTemperatureCoefficient()`.

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1
   
  * - Material
    - Electrical Resistivity
      [$\Omega\cdot\mathrm{m}$]
    - Temp. Coefficient
      [$1/\mathrm{K}$]
    - source
  
  * - `Copper`
    - $1.7241 \times 10^{-8}$
    - $3.93 \times 10^{-3}$
    - {cite:cts}`luecking_1981` p. 94
    
  * - `Aluminium`
    - $2.8264 \times 10^{-8}$
    - $4.03 \times 10^{-3}$
    - {cite:cts}`luecking_1981` p. 94
    
  * - `Lead`
    - $21.4 \times 10^{-8}$
    - $4.0 \times 10^{-3}$
    - {cite:cts}`luecking_1981` p. 94
    
  * - `Steel`
    - $13.8 \times 10^{-8}$
    - $4.5 \times 10^{-3}$
    - {cite:cts}`luecking_1981` p. 94
```

Calling these methods on non-conductive insulation materials will throw an `IllegalArgumentException`.

