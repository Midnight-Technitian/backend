CREATE TABLE dim_user
(
    id            SERIAL PRIMARY KEY,
    user_id       BIGINT UNIQUE NOT NULL,
    email         VARCHAR(255),
    ip_address    VARCHAR(45),
    registered_at TIMESTAMP NOT NULL
);

CREATE TABLE dim_ticket
(
    id          BIGINT NOT NULL,
    ticket_id   VARCHAR(255),
    service_id  VARCHAR(255),
    device_id   VARCHAR(255),
    employee_id VARCHAR(255),
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    claimed_at  TIMESTAMP WITHOUT TIME ZONE,
    closed_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_dim_ticket PRIMARY KEY (id)
);