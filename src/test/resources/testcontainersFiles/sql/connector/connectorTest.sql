CREATE TABLE public.test
(
    id SERIAL PRIMARY KEY ,
    a VARCHAR(255),
    b INT
);

INSERT INTO public.test(a, b) VALUES
    ('hello', 1),
    ('bye', 42);