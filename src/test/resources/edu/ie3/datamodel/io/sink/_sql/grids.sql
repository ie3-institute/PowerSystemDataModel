CREATE TABLE public.grids
(
    uuid uuid PRIMARY KEY,
    name TEXT NOT NULL
)
    WITHOUT OIDS
	TABLESPACE pg_default;

INSERT INTO
    public.grids (uuid, name)
VALUES
    ('8e6bd444-4580-11ee-be56-0242ac120002', 'vn_simona'),
    ('297dfac8-83cc-11ee-b962-0242ac120002', 'sampleGrid');