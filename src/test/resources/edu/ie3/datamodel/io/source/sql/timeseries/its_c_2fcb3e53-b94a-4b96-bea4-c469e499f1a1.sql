CREATE TABLE public."its_c_2fcb3e53-b94a-4b96-bea4-c469e499f1a1"
(
    time timestamp with time zone,
    price double precision,
    uuid uuid,
    CONSTRAINT its_c_pkey PRIMARY KEY (uuid)
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

INSERT INTO
    public."its_c_2fcb3e53-b94a-4b96-bea4-c469e499f1a1" (uuid, time, price)
VALUES
('45bd936f-524a-4d59-8978-31ccf37fa230', '2020-01-01T00:00:00Z', 100.0),
('41b8dbf6-3e75-4073-8359-89d015777dd6', '2020-01-01T00:15:00Z', 125.0);
