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

CREATE TABLE auth_refresh_token
(
    id          UUID   NOT NULL,
    user_email  VARCHAR(255) NOT NULL,
    token_hash  VARCHAR(128) NOT NULL,
    issued_at   TIMESTAMP     NOT NULL,
    expires_at  TIMESTAMP     NOT NULL,
    revoked_at  TIMESTAMP     NULL,
    replaced_by UUID        NULL,
    user_agent  VARCHAR(255) NULL,
    ip          VARCHAR(64)  NULL,
    CONSTRAINT pk_auth_refresh_token PRIMARY KEY (id)
);

ALTER TABLE auth_refresh_token
    ADD CONSTRAINT uc_auth_refresh_token_token_hash UNIQUE (token_hash);