# SQL


## Id Coordinate Source
The sql implementation of id coordinate source uses [PostgreSql](https://www.postgresql.org/) with the
addon [PostGis](https://postgis.net/). `PostGis` is used to improve the querying of geographical data.
The `Coordinate` attribute is stored as a [Geography](http://postgis.net/workshops/postgis-intro/geography.html) with
the type [Point](https://postgis.net/docs/ST_Point.html) and the default SRID 4326.

## Export

To export weather and coordinate tables to CSV, use the following queries:

```sql
COPY (
SELECT
	to_char(time, 'YYYY-MM-DD"T"HH24:MI:SS"Z"') AS time,
	coordinate_id,
	aswdifd_s,
	aswdir_s,
	t2m,
	u131m,
	v131m
FROM weathervalue
) TO '/tmp/weather.csv' CSV HEADER
```

```sql
COPY (
SELECT
	id,
	ST_X (coordinate::geometry) AS longitude,
	ST_Y (coordinate::geometry) AS latitude,
	'ICON' as coordinate_type
FROM coordinate
) to '/tmp/coordinates.csv' CSV HEADER
```

## SQL Inputs Overview

Here overview of the inputs from the SQL are provided for the grid models and participants. The below 
information shows the input parameters exported to csv.

### Operator
This is a simple identifier object, that represents one or more physical entitites of an input
controlled by the owner.

```sql
COPY (
SELECT
    uuid,
    id)
```

### The electrical grid input models and their inputs parameters exported to CSV are given in detail: 
### Node
Nodes represent connection points within the electrical grid. The input parameters for 
the nodes are given below:
```sql
COPY (
SELECT
    uuid,
    id,
    operator,
    operates_from,
    operates_until,
    v_target,
    slack,
    geo_position,
    volt_lvl,
    subnet
FROM node
) TO '/tmp/node_input.csv' CSV HEADER
```

### Line
AC lines represent electrical connections between nodes in the grid. 
The following line parameters are exported into a CSV file are given below:
```sql
COPY (
SELECT
    uuid,
    id,
    r,
    x,
    g,
    b,
    i_max,
    v_rated
FROM line
) TO '/tmp/line_input.csv' CSV HEADER
```

### Switch
Switches represent connections between two nodes of the same voltage level. The switch 
parameters that are exported to CSV are given below:
```sql
COPY (
SELECT
    uuid,
    id,
    operator,
    operates_from,
    operates_until,
    node_a,
    node_b,
    closed
FROM switch
) TO '/tmp/switch_input.csv' CSV HEADER
```

### Two-Winding Transformers
Two-winding transformers connect two voltage levels within the grid. 
The following transformer parameters from type and entity model are exported to CSV:

Type model:
```sql
COPY (
SELECT
    id,
    r_sc,
    x_sc,
    g_m,
    b_m,
    s_rated,
    v_rated_a,
    v_rated_b,
    d_v,
    d_phi,
    tap_side,
    tap_neutr,
    tap_min,
    tap_max
FROM two_winding_transformer
) TO '/tmp/transformer_2_w_type_input.csv' CSV HEADER
```

Entity model:
```sql
COPY (
SELECT
    uuid,
    id,
    operator,
    operates_from,
    operates_until,
    node_a,
    node_b,
    parallel_devices,
    type,
    tap_pos,
    auto_tap
FROM transformer
) TO '/tmp/transformer_2_w_input.csv' CSV HEADER
```

### Three Winding Transformer
Three-winding transformers connect three voltage levels within the grid.
The following transformer parameters from type and entity model are exported to CSV:

Type Model:
```sql
COPY (
SELECT
    uuid,
    id,
    r_sc_a,
    r_sc_b,
    r_sc_c,
    x_sc_a,
    x_sc_b,
    x_sc_c,
    g_m,
    b_m,
    s_rated_a,
    s_rated_b,
    s_rated_c,
    v_rated_a,
    v_rated_b,
    v_rated_c,
    d_v,
    d_phi,
    tap_neutr,
    tap_min,
    tap_max
FROM three_winding_transformer
) TO '/tmp/transformer_3_w_type_input.csv' CSV HEADER
```

Entity Model:
```sql
COPY (
SELECT
    uuid,
    id,
    operator,
    operates_from,
    operates_until,
    node_a,
    node_b,
    node_c,
    parallel_devices,
    type,
    tap_pos,
    auto_tap
FROM three_winding_transformer_entity
) TO '/tmp/transformer_3_w_input.csv' CSV HEADER
```

### The information about thermal grid related models are given below:

### Thermal Bus
Thermal buses represent coupling points within a thermal network in electrical grids. 
The following parameters from thermal bus data are exported to CSV:
```sql
COPY (
SELECT
    uuid,
    id,
    operator,
    operates_from,
    operates_until
FROM thermal_bus
) TO '/tmp/thermal_bus_input.csv' CSV HEADER
```

### Thermal House Model
Thermal house models describe the thermal behaviour of buildings connected 
to a thermal network. This reflects a simple shoe box with transmission losses.
The following parameters from thermal house model data are exported to CSV:

```sql
COPY (
SELECT
    uuid,
    id,
    thermal_bus,
    operator,
    operates_from,
    operates_until,
    eth_losses,
    eth_capa,
    target_temperature,
    upper_temperature_limit,
    lower_temperature_limit,
    housing_type,
    number_inhabitants
FROM thermal_house
) TO '/tmp/thermal_house_input.csv' CSV HEADER
```
### Cylindrical Thermal Storage
Cylindrical thermal storages represent using fluid to store thermal energy. The parameters 
that are exported to CSV are given below:
```sql
COPY (
SELECT
    uuid,
    id,
    operator,
    operates_from,
    operates_until,
    thermal_bus,
    storage_volume_lvl,
    inlet_temp,
    return_temp,
    c,
    p_thermal_max
FROM cylindrical_thermal_storage
) TO '/tmp/cylindrical_storage_input.csv' CSV HEADER
```

### Domestic Hot Water Storage
Domestic hot water storages represent thermal storage systems used for hot water supply in buildings.
The input parameters of domestic hot water storage that are exported to CSV are given below:
```sql
COPY (
SELECT
    uuid,
    id,
    operator,
    operates_from,
    operates_until,
    thermal_bus,
    storage_volume_lvl,
    inlet_temp,
    return_temp,
    c,
    p_thermal_max
FROM domestic_hot_water_storage
) TO '/tmp/domestic_water_storage_input.csv' CSV HEADER
```

### The grid participant models and their inputs parameters exported to CSV are given in detail:

### Biomass plant
Biomass plants represent generation units that produce electrical energy from biomass resources. 
The input parameters exported to CSV are given below:

type model:
```sql
COPY (
SELECT
    uuid,
    id,
    capex,
    opex,
    active_power_gradient,
    s_rated,
    cos_phi_rated,
    eta_conv
FROM biomass_plant_type
) TO '/tmp/biomass_type_input.csv' CSV HEADER
```

entity model:
```sql
COPY (
SELECT
    uuid,
    id,
    operator,
    operates_from,
    operates_until,
    node,
    q_characteristics,
    type,
    cost_controlled,
    feed_in_tariff,
    controlling_em
FROM biomass_plant
) TO '/tmp/biomass_input.csv' CSV HEADER
```

### Combined Heat and Power Plant
type model:
```sql
COPY (
SELECT
    uuid,
    id,
    capex,
    opex,
    eta_el,
    eta_thermal,
    s_rated,
    cos_phi_rated,
    p_thermal,
    p_own
FROM chp_plant_type
) TO '/tmp/chp__type_input.csv' CSV HEADER
```

entity model:
```sql
COPY (
SELECT
    uuid,
    id,
    operator,
    operates_from,
    operates_until,
    node,
    thermal_bus,
    q_characteristics,
    type,
    thermal_storage,
    controlling_em
FROM chp_plant
) TO '/tmp/chp_input.csv' CSV HEADER
```

### Electric Vehicle
Input parameters of the EVs used in the grid that are exported to CSV:

type model:
```sql
COPY (
SELECT
    uuid,
    id,
    capex,
    opex,
    e_storage,
    e_cons,
    s_rated,
    s_rated_dc,
    cos_phi_rated
FROM electric_vehicle_type
) TO '/tmp/ev_type_input.csv' CSV HEADER
```
entity model:
```sql
COPY (
SELECT
    uuid,
    id,
    operator,
    operates_from,
    operates_until,
    node,
    q_characteristics,
    type,
    controlling_em
FROM electric_vehicle
) TO '/tmp/ev_input.csv' CSV HEADER
```

### Electric Vehicle Charging Station
EVCS input parameters used in the grid that are exported to CSV:

entity model:
```sql
COPY (
SELECT
    uuid,
    id,
    operator,
    operates_from,
    operates_until,
    node,
    q_characteristics,
    type,
    charging_points,
    cos_phi_rated,
    location_type,
    v2g_support,
    controlling_em
FROM ev_charging_station
) TO '/tmp/evcs_input.csv' CSV HEADER
```

type model:
```sql
COPY (
SELECT
    id,
    s_rated,
    electric_current_type,
    synonymous_ids
FROM ev_charging_station_type
) TO '/tmp/evcs_type_input.csv' CSV HEADER
```

### Heat Pump
Heat pump parameters used to export to the CSV:

type model:
```sql
COPY (
SELECT
    uuid,
    id,
    capex,
    opex,
    s_rated,
    cos_phi_rated,
    p_thermal
FROM heat_pump_type
) TO '/tmp/hp_type_input.csv' CSV HEADER
```

entity model:
```sql
COPY (
SELECT
    uuid,
    id,
    operator,
    operates_from,
    operates_until,
    node,
    thermal_bus,
    q_characteristics,
    type,
    controlling_em
FROM heat_pump
) TO '/tmp/hp_input.csv' CSV HEADER
```

### Load
The inputs parameters from the Load used to export to the CSV:

```sql
COPY (
SELECT
    uuid,
    id,
    operator,
    operates_from,
    operates_until,
    node,
    q_characteristics,
    load_profile,
    e_cons_annual,
    s_rated,
    cos_phi_rated,
    controlling_em
FROM load
) TO '/tmp/load_input.csv' CSV HEADER
```

### Photovoltaic Power Plant
The inputs parameters from the PVs used to export to the CSV:
```sql
COPY (
SELECT
    uuid,
    id,
    operator,
    operates_from,
    operates_until,
    node,
    q_characteristics,
    albedo,
    azimuth,
    eta_conv,
    elevation_angle,
    k_g,
    k_t,
    s_rated,
    cos_phi_rated,
    controlling_em
FROM photovoltaic_power_plant
) TO '/tmp/pv_input.csv' CSV HEADER
```

### Electrical Energy Storage
The inputs parameters from the storage used to export to the CSV:

type model:
```sql
COPY (
SELECT
    uuid,
    id,
    capex,
    opex,
    e_storage,
    s_rated,
    cos_phi_rated,
    p_max,
    active_power_gradient,
    eta
FROM electrical_energy_storage_type
) TO '/tmp/storage_type_input.csv' CSV HEADER
```

entity model:
```sql
COPY (
SELECT
    uuid,
    id,
    operator,
    operates_from,
    operates_until,
    node,
    q_characteristics,
    type,
    controlling_em
FROM electrical_energy_storage
) TO '/tmp/storage_input.csv' CSV HEADER
```

### Wind Energy Converter
The inputs parameters from the wind energy converters used to export to the CSV:

type model:
```sql
COPY (
SELECT
    uuid,
    id,
    capex,
    opex,
    s_rated,
    cos_phi_rated,
    cp_characteristic,
    eta_conv,
    rotor_area,
    hub_height
FROM wind_energy_converter_type
) TO '/tmp/wec_type_input.csv' CSV HEADER
```

entity model:
```sql
COPY (
SELECT
    uuid,
    id,
    operator,
    operates_from,
    operates_until,
    node,
    q_characteristics,
    type,
    controlling_em
FROM wind_energy_converter
) TO '/tmp/wec_input.csv' CSV HEADER
```

### Energy Management Unit
Energy Management Units control the operation of connected system participants and 
can implement different control strategies. The input parameters of these energy management units
that are exported to CSV are given below:

```sql
COPY (
SELECT
    uuid,
    id,
    operator,
    operation_time,
    control_strategy,
    controlling_em
FROM energy_management_unit
) TO '/tmp/em_input.csv' CSV HEADER
```