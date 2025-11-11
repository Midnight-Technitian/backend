-- Flyway migration: create auth_refresh_token table
-- Stores hashed refresh tokens with rotation/revocation metadata

CREATE TABLE IF NOT EXISTS auth_refresh_token (
    id UUID PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL,
    token_hash VARCHAR(128) NOT NULL UNIQUE,
    issued_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP NULL,
    replaced_by UUID NULL,
    user_agent TEXT NULL,
    ip VARCHAR(64) NULL
);

CREATE INDEX IF NOT EXISTS idx_auth_refresh_token_user_email ON auth_refresh_token (user_email);
CREATE INDEX IF NOT EXISTS idx_auth_refresh_token_expires_at ON auth_refresh_token (expires_at);
