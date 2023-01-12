CREATE TABLE types.line_type_input
(
    uuid uuid NOT NULL,
    id varchar NOT NULL,
    v_rated double precision NOT NULL,
    i_max double precision NOT NULL,
    r double precision NOT NULL,
    x double precision NOT NULL,
    b double precision NOT NULL,
    g double precision NOT NULL,
    PRIMARY KEY (uuid)
);

CREATE TABLE types.transformer_2_w_type_input
(
    uuid uuid NOT NULL,
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
    v_rated_b double precision NOT NULL,
    PRIMARY KEY (uuid)
);

INSERT INTO
    types.line_type_input (uuid, id, v_rated, i_max, r, x, b, g)
VALUES
    ('3bed3eb3-9790-4874-89b5-a5434d408088', 'lineType_AtoB', 0.00322, 0.0, 0.437, 0.437, 300.0, 20.0);

INSERT INTO
    types.transformer_2_w_type_input (uuid, id, s_rated, r_sc, x_sc, b_m, g_m, d_phi, d_v, tap_max, tap_min, tap_neutr, tap_side, v_rated_a, v_rated_b)
VALUES
    ('202069a7-bcf8-422c-837c-273575220c8a', 'HS-MS_1', 45.375, 102.759, 20000.0, 110.0, 20.0, 0.0, 0.0, 1.5, 0.0);