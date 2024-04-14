CREATE TABLE IF NOT EXISTS banners (
    id INT PRIMARY KEY,
    tag_ids integer[],
    feature_id INT PRIMARY KEY,
    body jsonb,
    is_active bool,
    created_at DATE default now(),
    updated_at DATE default now());