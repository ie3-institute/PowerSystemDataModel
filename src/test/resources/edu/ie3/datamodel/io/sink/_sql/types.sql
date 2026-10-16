CREATE TABLE public.transformer_2_w_type_input
(
    uuid UUID PRIMARY KEY,
    b_m DOUBLE PRECISION NOT NULL,
    d_phi DOUBLE PRECISION NOT NULL,
    d_v DOUBLE PRECISION NOT NULL,
    g_m DOUBLE PRECISION NOT NULL,
    id TEXT NOT NULL,
    r_sc DOUBLE PRECISION NOT NULL,
    s_rated DOUBLE PRECISION NOT NULL,
    tap_max INT NOT NULL,
    tap_min INT NOT NULL,
    tap_neutr INT NOT NULL,
    tap_side BOOL NOT NULL,
    v_rated_a DOUBLE PRECISION NOT NULL,
    v_rated_b DOUBLE PRECISION NOT NULL,
    x_sc DOUBLE PRECISION NOT NULL,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.cable_type_input
(
    uuid UUID PRIMARY KEY,
    id varchar NOT NULL,
    core_number int NOT NULL,
    conductor jsonb NOT NULL,
    isolation jsonb NOT NULL,
    screen jsonb,
    filler jsonb NOT NULL,
    armor jsonb NOT NULL,
    jack jsonb NOT NULL,
    limit_temperature double precision NOT NULL,
    frequency double precision NOT NULL,
    skin_effect_coefficient double precision NOT NULL,
    proximity_effect_coefficient double precision NOT NULL,
    electrical_capacitance double precision NOT NULL,
    tan_delta double precision NOT NULL,
    circulating_loss_factor double precision NOT NULL,
    eddy_current_loss_factor double precision NOT NULL,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)   WITHOUT OIDS
	TABLESPACE pg_default;
CREATE INDEX idx_cable_type_conductor_material ON public.cable_type_input USING gin ((conductor->'material'));
CREATE INDEX idx_cable_type_id ON public.cable_type_input (id);

CREATE TABLE public.line_type_input
(
    uuid UUID PRIMARY KEY,
    b DOUBLE PRECISION NOT NULL,
    g DOUBLE PRECISION NOT NULL,
    i_max DOUBLE PRECISION NOT NULL,
    id TEXT NOT NULL,
    r DOUBLE PRECISION NOT NULL,
    v_rated DOUBLE PRECISION NOT NULL,
    x DOUBLE PRECISION NOT NULL,
    cable_type UUID REFERENCES public.cable_type_input(uuid),
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.storage_type_input
(
    uuid UUID PRIMARY KEY,
    active_power_gradient DOUBLE PRECISION NOT NULL,
    capex DOUBLE PRECISION NOT NULL,
    cos_phi_rated TEXT NOT NULL,
    e_storage DOUBLE PRECISION NOT NULL,
    eta DOUBLE PRECISION NOT NULL,
    id TEXT NOT NULL,
    opex DOUBLE PRECISION NOT NULL,
    p_max DOUBLE PRECISION NOT NULL,
    s_rated DOUBLE PRECISION NOT NULL,
    grid_uuid UUID NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;
