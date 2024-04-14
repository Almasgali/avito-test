DROP SEQUENCE IF EXISTS banners_seq;

CREATE SEQUENCE banners_seq START 1;

DROP TABLE IF EXISTS banners;

CREATE TABLE banners (
    id SERIAL PRIMARY KEY,
    tag_ids integer[],
    feature_id INT,
    body jsonb,
    is_active bool,
    created_at timestamp default now(),
    updated_at timestamp default now());