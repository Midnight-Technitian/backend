-- Create tables
CREATE TABLE IF NOT EXISTS services (
    service_id BIGINT PRIMARY KEY,
    service_name VARCHAR(255) NOT NULL,
    service_description TEXT,
    service_price DECIMAL(10, 2) NOT NULL,
    fixed_rate BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS customers (
    customer_id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    created_at DATE NOT NULL,
    updated_at DATE,
    contact_number VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS customer_device (
    device_id BIGINT PRIMARY KEY,
    customer_email VARCHAR(255) NOT NULL,
    device_name VARCHAR(255) NOT NULL,
    device_type VARCHAR(50) NOT NULL,
    device_info VARCHAR(255) NOT NULL,
    created_at DATE NOT NULL,
    updated_at DATE
);

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

-- Services
INSERT INTO services (service_id, service_name, service_description, service_price, fixed_rate)
VALUES (1, 'Destroy', '32-pass Data Erase; Followed with physical destruction.', 22.00, TRUE),
       (2, 'Re-Image', 'Reinstall the Operating System of a PC/Tablet/Laptop/Mobile device.', 50.00, TRUE),
       (3, 'Backup', 'Create an archived backup of all essential and user files.', 25.00, TRUE),
       (4, 'Diagnostic', 'Check the health of the physical Hardware, as well as scan for malicious software', 40.00, TRUE),
       (5, 'Hardware Upgrade', 'Install new Hardware and Drivers + Device Hardware health Scan.', 20.00, TRUE),
       (6, 'On-site Tech', 'On-Site technician, $99/first hr then $40/hr additional.', 99.00, FALSE);