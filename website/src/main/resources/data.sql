-- Create tables
CREATE TABLE IF NOT EXISTS user_profile (
    uid BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    contact_number VARCHAR(255) NOT NULL,
    encrypted_password VARCHAR(255) NOT NULL,
    created_at TEXT,
    updated_at TEXT
);

CREATE TABLE IF NOT EXISTS user_role (
    uid   BIGSERIAL NOT NULL PRIMARY KEY,
    email varchar(255) NOT NULL,
    role  varchar(255) NOT NULL
);