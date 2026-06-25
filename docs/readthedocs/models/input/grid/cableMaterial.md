# Cable Material

## Overview

The `CableMaterial` enum represents various materials used in electrical cable construction, providing their default thermal and electrical properties. This is primarily used for physical modeling and simulation of distribution grids, allowing for accurate calculations of thermal states, ampacity, and electrical losses.

## Properties

### Thermal Properties

Retrieved via `getThermalProperties()`. Returns a `ThermalProperties` container providing **Thermal Resistivity** ($K \cdot m/W$) and **Thermal Capacitance** ($J / (m^3 \cdot K)$).

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1
   
  * - Material
    - Thermal Resistivity []
    - Thermal Capacitance [1/K]
    - source 
    - Notes
  
  * - `Copper`
    - 1/384 
    - 3,449,600.0
    - {cite:cts}`wiki:thermal_conductivity_resistivity`, {cite:cts}`wiki:Copper` 
    - c = 385 J/(kg * K), rho= 8.96 g/cm³ => 3449600 J / (m³ * K)
    
  * - `Aluminium`
    - 1/237
    - 2,420,913.3 
    - {cite:cts}`wiki:thermal_conductivity_resistivity`, {cite:cts}`wiki:Aluminium` 
    - c = 897 J/(kg * K), rho= 2.6989 g/cm³ => 2420913.3 J / (m³ * K)
    
  * - `XLPE (Cross-linked polyethylene)`
    - 3.5
    - 2.4
    - {cite:cts}`andersRatingElectricPower1997` p. 400
    
  * - `PE (Polyethylene)`
    - 3.5
    - 2.4
    - {cite:cts}`andersRatingElectricPower1997` p. 400
    
  * - `PVC (Polyvinyl chloride)`
    - 3.5
    - 1.7
    - {cite:cts}`andersRatingElectricPower1997` p. 400
    
  * - `Semi-Conductive Screen`
    - 2.5
    - 2.4
    - Th. Res.: {cite:cts}`CIGRE_TB880_2022` p. 28; Th. Capa.: Same as adjacent dielectric material see {cite:cts}`andersRatingElectricPower1997` p. 400

  * - `SC-Tape (Screen Tape)`
    - 6.0
    - 2.4
    - Th. Res.: {cite:cts}`CIGRE_TB880_2022` p. 28; Th. Capa.: Same as adjacent dielectric material see {cite:cts}`andersRatingElectricPower1997` p. 400
    
  * - `Lead`
    - 1/35
    - 1,463,892.0
    - Th. Res.: {cite:cts}`wiki:thermal_conductivities`; Th. Capa.: {cite:cts}`wiki:wiki:specific_heat_capacities` 
    - c = 129 J/(kg * K), rho= 11.348 g/cm³ => 1,463,892.0 J / (m³ * K)
    
  * - `Steel`
    - 1/45
    - 3,756,000.0
    - Th. Res.:{cite:cts}`wiki:thermal_conductivity_resistivity`; Th. Capa.: {cite:cts}`wiki:wiki:specific_heat_capacities` 
    
  * - `Polypropylen`
    - 6.0
    - 2.0
    - Th. Res.: {cite:cts}`CIGRE_TB880_2022` p. 28; Th. Capa.: Asumed to be clos to Paper-polypropylene-paper (PPL) in {cite:cts}`andersRatingElectricPower1997` p. 400
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
    - Electrical Resistivity []
    - Temp. Coefficient [1/K]
    - source
  
  * -`Copper`
    - $1.7241 \times 10^{-8}$
    - $3.93 \times 10^{-3}$
    - {cite:cts}`luecking_1981` p. 94
    
   * -`Aluminium`
    - $2.8264 \times 10^{-8}$
    - $4.03 \times 10^[-3}$
    - {cite:cts}`luecking_1981` p. 94
    
   * -`Lead`
    - $21.4 \times 10^{-8}$
    - $4.0 \times 10^[-3}$
    - {cite:cts}`luecking_1981` p. 94
    
   * -`Steel`
    - $13.8 \times 10^{-8}$
    - $4.5 \times 10^[-3}$
    - {cite:cts}`luecking_1981` p. 94
```

Calling these methods on non-conductive insulation materials will throw an `IllegalArgumentException`.