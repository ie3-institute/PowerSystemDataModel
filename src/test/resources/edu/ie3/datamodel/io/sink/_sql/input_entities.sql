CREATE TABLE public.node_input
(
    uuid uuid PRIMARY KEY,
    geo_position TEXT NOT NULL,
    id TEXT NOT NULL,
    operates_from timestamp with time zone,
    operates_until timestamp with time zone,
    operator uuid,
    slack bool NOT NULL,
    subnet int NOT NULL,
    v_rated double precision NOT NULL,
    v_target double precision NOT NULL,
    volt_lvl TEXT NOT NULL,
    grid_uuid uuid NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.transformer_2_w_input
(
    uuid uuid PRIMARY KEY,
    auto_tap bool NOT NULL,
    id TEXT NOT NULL,
    node_a uuid NOT NULL REFERENCES node_input(uuid),
    node_b uuid NOT NULL REFERENCES node_input(uuid),
    operates_from timestamp with time zone,
    operates_until timestamp with time zone,
    operator uuid,
    parallel_devices int NOT NULL,
    tap_pos int NOT NULL,
    type uuid NOT NULL REFERENCES transformer_2_w_type_input(uuid),
    grid_uuid uuid NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.evcs_input
(
    uuid uuid PRIMARY KEY,
    charging_points int NOT NULL,
    controlling_em uuid,
    cos_phi_rated TEXT NOT NULL,
    id TEXT NOT NULL,
    location_type TEXT NOT NULL,
    node uuid NOT NULL,
    operates_from timestamp with time zone,
    operates_until timestamp with time zone,
    operator uuid,
    q_characteristics TEXT NOT NULL,
    type TEXT NOT NULL,
    v_2g_support bool NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.line_graphic_input
(
    uuid uuid PRIMARY KEY,
    graphic_layer TEXT NOT NULL,
    line uuid NOT NULL,
    path TEXT,
    grid_uuid uuid NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.node_graphic_input
(
    uuid uuid PRIMARY KEY,
    graphic_layer TEXT NOT NULL,
    node uuid NOT NULL,
    path TEXT,
    point TEXT NOT NULL,
    grid_uuid uuid NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.cylindrical_storage_input
(
    uuid uuid PRIMARY KEY,
    c double precision NOT NULL,
    id TEXT NOT NULL,
    inlet_temp double precision NOT NULL,
    operates_from timestamp with time zone,
    operates_until timestamp with time zone,
    operator uuid,
    return_temp double precision NOT NULL,
    storage_volume_lvl double precision NOT NULL,
    storage_volume_lvl_min double precision NOT NULL,
    thermal_bus uuid NOT NULL,
    grid_uuid uuid NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.thermal_house_input
(
    uuid uuid PRIMARY KEY,
    eth_capa double precision NOT NULL,
    eth_losses double precision NOT NULL,
    id TEXT NOT NULL,
    lower_temperature_limit double precision NOT NULL,
    operates_from timestamp with time zone,
    operates_until timestamp with time zone,
    operator uuid,
    target_temperature double precision NOT NULL,
    thermal_bus uuid NOT NULL,
    upper_temperature_limit double precision NOT NULL,
    grid_uuid uuid NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.operator_input
(
    uuid uuid PRIMARY KEY,
    id TEXT NOT NULL,
    grid_uuid uuid NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.line_input
(
    uuid uuid PRIMARY KEY,
    geo_position TEXT NOT NULL,
    id TEXT NOT NULL,
    length double precision NOT NULL,
    node_a uuid NOT NULL,
    node_b uuid NOT NULL,
    olm_characteristic TEXT NOT NULL,
    operates_from timestamp with time zone,
    operates_until timestamp with time zone,
    operator uuid,
    parallel_devices int NOT NULL,
    type uuid NOT NULL,
    grid_uuid uuid NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.thermal_bus_input
(
    uuid uuid PRIMARY KEY,
    id TEXT NOT NULL,
    operates_from timestamp with time zone,
    operates_until timestamp with time zone,
    operator uuid,
    grid_uuid uuid NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.load_input
(
    uuid uuid PRIMARY KEY,
    controlling_em uuid NOT NULL,
    cos_phi_rated TEXT NOT NULL,
    dsm bool NOT NULL,
    e_cons_annual double precision NOT NULL,
    id TEXT NOT NULL,
    load_profile TEXT NOT NULL,
    node uuid NOT NULL REFERENCES node_input(uuid),
    operates_from timestamp with time zone,
    operates_until timestamp with time zone,
    operator uuid,
    q_characteristics TEXT NOT NULL,
    s_rated double precision NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.em_input
(
    uuid uuid PRIMARY KEY,
    control_strategy TEXT NOT NULL,
    controlling_em uuid,
    id TEXT NOT NULL,
    operates_from timestamp with time zone,
    operates_until timestamp with time zone,
    operator uuid,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.pv_input
(
    uuid uuid PRIMARY KEY,
    albedo double precision NOT NULL,
    azimuth double precision NOT NULL,
    controlling_em uuid,
    cos_phi_rated TEXT NOT NULL,
    elevation_angle double precision NOT NULL,
    eta_conv double precision NOT NULL,
    id TEXT NOT NULL,
    k_g double precision NOT NULL,
    k_t double precision NOT NULL,
    market_reaction bool NOT NULL,
    node uuid NOT NULL,
    operates_from timestamp with time zone,
    operates_until timestamp with time zone,
    operator uuid,
    q_characteristics TEXT NOT NULL,
    s_rated double precision NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.storage_input
(
    uuid uuid PRIMARY KEY,
    controlling_em uuid NOT NULL,
    id TEXT NOT NULL,
    node uuid NOT NULL,
    operates_from timestamp with time zone,
    operates_until timestamp with time zone,
    operator uuid,
    q_characteristics TEXT NOT NULL,
    type uuid NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;