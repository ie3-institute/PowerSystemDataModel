CREATE TABLE public.node_input
(
    uuid UUID PRIMARY KEY,
    geo_position TEXT NOT NULL,
    id TEXT NOT NULL,
    operates_from TIMESTAMP WITH TIME ZONE,
    operates_until TIMESTAMP WITH TIME ZONE,
    operator UUID,
    slack BOOL NOT NULL,
    subnet int NOT NULL,
    v_rated DOUBLE PRECISION NOT NULL,
    v_target DOUBLE PRECISION NOT NULL,
    volt_lvl TEXT NOT NULL,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.transformer_2_w_input
(
    uuid UUID PRIMARY KEY,
    auto_tap BOOL NOT NULL,
    id TEXT NOT NULL,
    node_a UUID NOT NULL REFERENCES node_input(uuid),
    node_b UUID NOT NULL REFERENCES node_input(uuid),
    operates_from TIMESTAMP WITH TIME ZONE,
    operates_until TIMESTAMP WITH TIME ZONE,
    operator UUID,
    parallel_devices int NOT NULL,
    tap_pos int NOT NULL,
    type UUID NOT NULL REFERENCES transformer_2_w_type_input(uuid),
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.evcs_input
(
    uuid UUID PRIMARY KEY,
    charging_points int NOT NULL,
    controlling_em UUID,
    cos_phi_rated TEXT NOT NULL,
    id TEXT NOT NULL,
    location_type TEXT NOT NULL,
    node UUID NOT NULL,
    operates_from TIMESTAMP WITH TIME ZONE,
    operates_until TIMESTAMP WITH TIME ZONE,
    operator UUID,
    q_characteristics TEXT NOT NULL,
    type TEXT NOT NULL,
    v_2g_support BOOL NOT NULL,
    grid_uuid UUID NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.line_graphic_input
(
    uuid UUID PRIMARY KEY,
    graphic_layer TEXT NOT NULL,
    line UUID NOT NULL,
    path TEXT,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.node_graphic_input
(
    uuid UUID PRIMARY KEY,
    graphic_layer TEXT NOT NULL,
    node UUID NOT NULL,
    path TEXT,
    point TEXT NOT NULL,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.cylindrical_storage_input
(
    uuid UUID PRIMARY KEY,
    c DOUBLE PRECISION NOT NULL,
    id TEXT NOT NULL,
    inlet_temp DOUBLE PRECISION NOT NULL,
    operates_from TIMESTAMP WITH TIME ZONE,
    operates_until TIMESTAMP WITH TIME ZONE,
    operator UUID,
    return_temp DOUBLE PRECISION NOT NULL,
    storage_volume_lvl DOUBLE PRECISION NOT NULL,
    storage_volume_lvl_min DOUBLE PRECISION NOT NULL,
    thermal_bus UUID NOT NULL,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.thermal_house_input
(
    uuid UUID PRIMARY KEY,
    eth_capa DOUBLE PRECISION NOT NULL,
    eth_losses DOUBLE PRECISION NOT NULL,
    id TEXT NOT NULL,
    lower_temperature_limit DOUBLE PRECISION NOT NULL,
    operates_from TIMESTAMP WITH TIME ZONE,
    operates_until TIMESTAMP WITH TIME ZONE,
    operator UUID,
    target_temperature DOUBLE PRECISION NOT NULL,
    thermal_bus UUID NOT NULL,
    upper_temperature_limit DOUBLE PRECISION NOT NULL,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.operator_input
(
    uuid UUID PRIMARY KEY,
    id TEXT NOT NULL,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.line_input
(
    uuid UUID PRIMARY KEY,
    geo_position TEXT NOT NULL,
    id TEXT NOT NULL,
    length DOUBLE PRECISION NOT NULL,
    node_a UUID NOT NULL,
    node_b UUID NOT NULL,
    olm_characteristic TEXT NOT NULL,
    operates_from TIMESTAMP WITH TIME ZONE,
    operates_until TIMESTAMP WITH TIME ZONE,
    operator UUID,
    parallel_devices int NOT NULL,
    type UUID NOT NULL,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.thermal_bus_input
(
    uuid UUID PRIMARY KEY,
    id TEXT NOT NULL,
    operates_from TIMESTAMP WITH TIME ZONE,
    operates_until TIMESTAMP WITH TIME ZONE,
    operator UUID,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.load_input
(
    uuid UUID PRIMARY KEY,
    controlling_em UUID NOT NULL,
    cos_phi_rated TEXT NOT NULL,
    dsm BOOL NOT NULL,
    e_cons_annual DOUBLE PRECISION NOT NULL,
    id TEXT NOT NULL,
    load_profile TEXT NOT NULL,
    node UUID NOT NULL REFERENCES node_input(uuid),
    operates_from TIMESTAMP WITH TIME ZONE,
    operates_until TIMESTAMP WITH TIME ZONE,
    operator UUID,
    q_characteristics TEXT NOT NULL,
    s_rated DOUBLE PRECISION NOT NULL,
    grid_uuid UUID NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.em_input
(
    uuid UUID PRIMARY KEY,
    control_strategy TEXT NOT NULL,
    controlling_em UUID,
    id TEXT NOT NULL,
    operates_from TIMESTAMP WITH TIME ZONE,
    operates_until TIMESTAMP WITH TIME ZONE,
    operator UUID,
    grid_uuid UUID NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.pv_input
(
    uuid UUID PRIMARY KEY,
    albedo DOUBLE PRECISION NOT NULL,
    azimuth DOUBLE PRECISION NOT NULL,
    controlling_em UUID,
    cos_phi_rated TEXT NOT NULL,
    elevation_angle DOUBLE PRECISION NOT NULL,
    eta_conv DOUBLE PRECISION NOT NULL,
    id TEXT NOT NULL,
    k_g DOUBLE PRECISION NOT NULL,
    k_t DOUBLE PRECISION NOT NULL,
    market_reaction BOOL NOT NULL,
    node UUID NOT NULL,
    operates_from TIMESTAMP WITH TIME ZONE,
    operates_until TIMESTAMP WITH TIME ZONE,
    operator UUID,
    q_characteristics TEXT NOT NULL,
    s_rated DOUBLE PRECISION NOT NULL,
    grid_uuid UUID NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.storage_input
(
    uuid UUID PRIMARY KEY,
    controlling_em UUID NOT NULL,
    id TEXT NOT NULL,
    node UUID NOT NULL,
    operates_from TIMESTAMP WITH TIME ZONE,
    operates_until TIMESTAMP WITH TIME ZONE,
    operator UUID,
    q_characteristics TEXT NOT NULL,
    type UUID NOT NULL,
    grid_uuid UUID NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;