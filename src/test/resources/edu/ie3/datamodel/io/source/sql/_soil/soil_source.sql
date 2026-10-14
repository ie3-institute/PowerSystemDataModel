CREATE TABLE public.soil_type
(
    uuid uuid PRIMARY KEY,
    id text NOT NULL,
    thermal_resistivity_wet double precision NOT NULL,
    thermal_resistivity_dry double precision NOT NULL,
    specific_heat_capacity double precision NOT NULL,
    critical_temperature_difference double precision NOT NULL
);

CREATE TABLE public.soil_layer
(
    uuid uuid PRIMARY KEY,
    geometry text NOT NULL,
    z_from double precision NOT NULL,
    z_to double precision NOT NULL,
    soil_type uuid NOT NULL
);

INSERT INTO public.soil_type
    (uuid, id, thermal_resistivity_wet, thermal_resistivity_dry, specific_heat_capacity, critical_temperature_difference)
VALUES
    ('32b43a78-7721-431d-b1c2-56975a123670', 'loam', 0.3, 0.4, 0.0015, 15.0);

INSERT INTO public.soil_layer
    (uuid, geometry, z_from, z_to, soil_type)
VALUES
    ('123e4567-e89b-12d3-a456-426614174000', '{"type":"Polygon","coordinates":[[[0,0],[1,0],[1,1],[0,1],[0,0]]]}', 0.0, -2.0, '32b43a78-7721-431d-b1c2-56975a123670');
