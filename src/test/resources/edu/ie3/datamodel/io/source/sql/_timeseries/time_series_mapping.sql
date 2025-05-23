CREATE TABLE public.time_series_mapping
(
    asset uuid PRIMARY KEY,
    time_series uuid
)
    WITHOUT OIDS
    TABLESPACE pg_default;

INSERT INTO
    public.time_series_mapping (asset, time_series)
VALUES
('b86e95b0-e579-4a80-a534-37c7a470a409', '9185b8c1-86ba-4a16-8dea-5ac898e8caa5'),
('c7ebcc6c-55fc-479b-aa6b-6fa82ccac6b8', '3fbfaa97-cff4-46d4-95ba-a95665e87c26'),
('90a96daa-012b-4fea-82dc-24ba7a7ab81c', '3fbfaa97-cff4-46d4-95ba-a95665e87c26');
