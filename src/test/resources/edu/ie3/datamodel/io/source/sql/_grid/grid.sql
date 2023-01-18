CREATE TABLE grid.line_input
(
    uuid uuid NOT NULL,
    id varchar NOT NULL,
    operator uuid references grid.operator_input,
    operation_start_date timestamp with time zone,
    operation_end_date timestamp with time zone,
    node_a uuid references node_input NOT NULL,
    node_b uuid references node_input NOT NULL,
    olm_characteristic varchar NOT NULL,
    parallel_devices int NOT NULL,
    length double precision NOT NULL,
    type uuid references types.line_type_input,
    PRIMARY KEY (uuid)
);

CREATE TABLE grid.transformer_2_w_input
(
    uuid uuid NOT NULL,
    id varchar NOT NULL,
    operator uuid references grid.operator_input,
    operates_from timestamp with time zone,
    operates_until timestamp with time zone,
    node_a uuid references input.node_input,
    node_b uuid references input.node_input,
    auto_tap bool NOT NULL,
    parallel_devices int NOT NULL,
    tap_pos int NOT NULL,
    type uuid references types.transformer_2_w_type_input,
    PRIMARY KEY(uuid)
);

INSERT INTO
    grid.line_input (uuid, id, operator, operation_start_date, operation_end_date, node_a, node_b, olm_characteristic, parallel_devices, length, type)
VALUES
    ('92ec3bcf-1777-4d38-af67-0bf7c9fa73c7', '{""type"":""LineString"",""coordinates"":[[7.411111,51.492528],[7.414116,51.484136]],""crs"":{""type"":""name"",""properties"":{""name"":""EPSG:4326""}}}', '2020-03-24T15:11:31Z','2020-03-25T15:11:31Z', '4ca90220-74c2-4369-9afa-a18bf068840d', '47d29df0-ba2d-4d23-8e75-c82229c5c758', 'olm:{(0.00,1.00)}',2,0.03,'3bed3eb3-9790-4874-89b5-a5434d408088');

INSERT INTO
    grid.transformer_2_w_input (uuid,auto_tap,id,node_a,node_b,operates_from,operates_until,operator,parallel_devices,tap_pos,type)
VALUES
    ('58247de7-e297-4d9b-a5e4-b662c058c655',true,'2w_single_test','47d29df0-ba2d-4d23-8e75-c82229c5c758','6e0980e0-10f2-4e18-862b-eb2b7c90509b',,,,1,0,'202069a7-bcf8-422c-837c-273575220c8a')

