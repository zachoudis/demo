CREATE TABLE IF NOT EXISTS devices (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    state VARCHAR(50) NOT NULL,
    creation_time TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_devices_brand ON devices (brand);
CREATE INDEX IF NOT EXISTS idx_devices_state ON devices (state);
