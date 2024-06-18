CREATE TABLE public.pv_res
(
    input_model uuid NOT NULL,
    p double precision NOT NULL,
    q double precision NOT NULL,
    time timestamp with time zone NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.wec_res
(
    input_model uuid NOT NULL,
    p double precision NOT NULL,
    q double precision NOT NULL,
    time timestamp with time zone NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.ev_res
(
    input_model uuid NOT NULL,
    p double precision NOT NULL,
    q double precision NOT NULL,
    soc double precision NOT NULL,
    time timestamp with time zone NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.evcs_res
(
    input_model uuid NOT NULL,
    p double precision NOT NULL,
    q double precision NOT NULL,
    time timestamp with time zone NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.em_res
(
    input_model uuid NOT NULL,
    p double precision NOT NULL,
    q double precision NOT NULL,
    time timestamp with time zone NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;

CREATE TABLE public.flex_options_res
(
    input_model uuid NOT NULL,
    p_max double precision NOT NULL,
    p_min double precision NOT NULL,
    p_ref double precision NOT NULL,
    time timestamp with time zone NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;
