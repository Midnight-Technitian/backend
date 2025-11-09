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


CREATE SEQUENCE dim_user_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE dim_ticket_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE dim_user
    ALTER COLUMN id SET DEFAULT nextval('dim_user_seq');
ALTER TABLE dim_ticket
    ALTER COLUMN id SET DEFAULT nextval('dim_ticket_seq');


-- Email analytics dimension table
CREATE TABLE dim_emails (
    id SERIAL PRIMARY KEY,
    template_name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Email analytics fact table
CREATE TABLE fact_email_activity (
    id SERIAL PRIMARY KEY,
    email_id VARCHAR(100) UNIQUE NOT NULL,
    recipient VARCHAR(255),
    template_id INT REFERENCES dim_emails(id),
    triggered_by VARCHAR(100),
    service_origin VARCHAR(100),
    sent_at TIMESTAMP NOT NULL,
    status VARCHAR(20),
    latency_ms BIGINT,
    created_at TIMESTAMP DEFAULT NOW()
);
