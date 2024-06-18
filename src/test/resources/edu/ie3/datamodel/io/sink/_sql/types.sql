CREATE TABLE public.transformer_2_w_type_input
(
    uuid uuid PRIMARY KEY,
    b_m double precision NOT NULL,
    d_phi double precision NOT NULL,
    d_v double precision NOT NULL,
    g_m double precision NOT NULL,
    id TEXT NOT NULL,
    r_sc double precision NOT NULL,
    s_rated double precision NOT NULL,
    tap_max int NOT NULL,
    tap_min int NOT NULL,
    tap_neutr int NOT NULL,
    tap_side bool NOT NULL,
    v_rated_a double precision NOT NULL,
    v_rated_b double precision NOT NULL,
    x_sc double precision NOT NULL,
    grid_uuid uuid NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.line_type_input
(
    uuid uuid PRIMARY KEY,
    b double precision NOT NULL,
    g double precision NOT NULL,
    i_max double precision NOT NULL,
    id TEXT NOT NULL,
    r double precision NOT NULL,
    v_rated double precision NOT NULL,
    x double precision NOT NULL,

    grid_uuid uuid NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.storage_type_input
(
    uuid uuid PRIMARY KEY,
    active_power_gradient double precision NOT NULL,
    capex double precision NOT NULL,
    cos_phi_rated TEXT NOT NULL,
    e_storage double precision NOT NULL,
    eta double precision NOT NULL,
    id TEXT NOT NULL,
    opex double precision NOT NULL,
    p_max double precision NOT NULL,
    s_rated double precision NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;
