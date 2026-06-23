CREATE TABLE public.cable_type_input
(
    uuid uuid NOT NULL,
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
    PRIMARY KEY (uuid)
);

CREATE INDEX idx_cable_type_conductor_material ON public.cable_type_input USING gin ((conductor->'material'));
CREATE INDEX idx_cable_type_id ON public.cable_type_input (id);

CREATE TABLE public.line_type_input
(
    uuid uuid NOT NULL,
    id varchar NOT NULL,
    v_rated double precision NOT NULL,
    i_max double precision NOT NULL,
    r double precision NOT NULL,
    x double precision NOT NULL,
    b double precision NOT NULL,
    g double precision NOT NULL,
    cable_type UUID REFERENCES public.cable_type_input(uuid),
    PRIMARY KEY (uuid)
);

CREATE TABLE public.transformer_2_w_type_input
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

INSERT INTO public.cable_type_input
(uuid, id, core_number, conductor, isolation, screen, filler, armor, jack,
 limit_temperature, frequency, skin_effect_coefficient, proximity_effect_coefficient,
 electrical_capacitance, tan_delta, circulating_loss_factor, eddy_current_loss_factor)
VALUES
    ('b8152c3f-d12f-4857-9746-a30aef6aee08',
     'CigreT880_33kVLandCable',
     1,
     '{"id": "conductor", "material": "COPPER", "crossSection": "240.0", "diameter": "18.4", "thermalResistivity": "0.0026", "thermalCapacitance": "3.4e6", "area": "240.0", "isCompacted": false}'::jsonb,
     '[{"name": "conductorScreen", "material": "SEMI_COND_SCREEN", "innerDiameter": "18.4", "outerDiameter": "19.4", "thermalResistivity": "4.0", "thermalCapacitance": "2.0e6", "area": null}, {"name": "insulation", "material": "XLPE", "innerDiameter": "19.4", "outerDiameter": "34.8", "thermalResistivity": "3.5", "thermalCapacitance": "2.4e6", "area": null}, {"name": "insulationScreen", "material": "SEMI_COND_SCREEN", "innerDiameter": "34.8", "outerDiameter": "35.8", "thermalResistivity": "4.0", "thermalCapacitance": "2.0e6", "area": null}, {"name": "screenTape", "material": "SC_TAPE", "innerDiameter": "35.8", "outerDiameter": "36.8", "thermalResistivity": "0.01", "thermalCapacitance": "3.0e6", "area": null}]'::jsonb,
     '{"name": "screen", "material": "COPPER", "innerDiameter": "36.8", "outerDiameter": "38.6", "thermalResistivity": "0.0026", "thermalCapacitance": "3.4e6", "area": "35.62566", "wiresNumber": 56, "wireDiameter": "0.9", "electricalResistivity": "1.7241e-8"}'::jsonb,
     '[]'::jsonb,
     '[]'::jsonb,
     '[{"name": "jackTape", "material": "SC_TAPE", "innerDiameter": "38.6", "outerDiameter": "39.2", "thermalResistivity": "0.01", "thermalCapacitance": "3.0e6", "area": null}, {"name": "jack", "material": "XLPE", "innerDiameter": "39.2", "outerDiameter": "43.6", "thermalResistivity": "3.5", "thermalCapacitance": "2.4e6", "area": null}, {"name": "outerCover", "material": "SEMI_COND_SCREEN", "innerDiameter": "43.6", "outerDiameter": "44.0", "thermalResistivity": "4.0", "thermalCapacitance": "2.0e6", "area": null}]'::jsonb,
     90.0,
     50.0,
     1.0,
     1.0,
     0.000000000237683304,
     0.004,
     0.0435122656,
     0.0);

INSERT INTO public.line_type_input (uuid, id, v_rated, i_max, r, x, b, g, cable_type,grid_uuid)
VALUES
    ('3bed3eb3-9790-4874-89b5-a5434d408088', 'lineType_AtoB', 20.0, 300.0, 0.437, 0.356, 0.00322, 0.0, 'b8152c3f-d12f-4857-9746-a30aef6aee08','8e6bd444-4580-11ee-be56-0242ac120002');

INSERT INTO public.transformer_2_w_type_input (uuid,b_m,d_phi,d_v,g_m,id,r_sc,s_rated,tap_max,tap_min,tap_neutr,tap_side,v_rated_a,v_rated_b,x_sc,grid_uuid)
VALUES
    ('202069a7-bcf8-422c-837c-273575220c8a',0.0,0.0,1.5,0.0,'HS-MS_1',45.375,20000.0,10,-10,0,false,110.0,20.0,102.759,'8e6bd444-4580-11ee-be56-0242ac120002');