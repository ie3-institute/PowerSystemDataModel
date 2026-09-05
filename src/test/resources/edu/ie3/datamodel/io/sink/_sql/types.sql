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
    eddy_current_loss_factor double precision NOT NULL
)   WITHOUT OIDS
	TABLESPACE pg_default;
CREATE INDEX idx_cable_type_conductor_material ON public.cable_type_input USING gin ((conductor->'material'));
CREATE INDEX idx_cable_type_id ON public.cable_type_input (id);

CREATE TABLE public.line_type_input
(
    uuid UUID PRIMARY KEY,
    id varchar NOT NULL,
    v_rated double precision NOT NULL,
    i_max double precision NOT NULL,
    r double precision NOT NULL,
    x double precision NOT NULL,
    b double precision NOT NULL,
    g double precision NOT NULL,
    cable_type UUID REFERENCES public.cable_type_input(uuid)
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.transformer_2_w_type_input
(
    uuid UUID PRIMARY KEY,
    id varchar NOT NULL,
    s_rated double precision NOT NULL,
    r_sc double precision NOT NULL,
    x_sc double precision NOT NULL,
    b_m double precision NOT NULL,
    g_m double precision NOT NULL,
    d_phi double precision NOT NULL,
    d_v double precision NOT NULL,
    tap_max int NOT NULL,
    tap_min int NOT NULL,
    tap_neutr int NOT NULL,
    tap_side bool NOT NULL,
    v_rated_a double precision NOT NULL,
    v_rated_b double precision NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.storage_type_input
(
    uuid UUID PRIMARY KEY,
    id varchar NOT NULL,
    capex double precision,
    opex double precision,
    s_rated double precision NOT NULL,
    cos_phi_rated double precision NOT NULL,
    e_storage double precision NOT NULL,
    p_max double precision NOT NULL,
    active_power_gradient double precision NOT NULL,
    eta double precision NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;
