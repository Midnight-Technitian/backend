-- Create tables
CREATE TABLE IF NOT EXISTS services (
    service_id BIGINT PRIMARY KEY,
    service_name VARCHAR(255) NOT NULL,
    service_description TEXT,
    service_price DECIMAL(10, 2) NOT NULL,
    fixed_rate BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS employees (
    employee_id VARCHAR(50) PRIMARY KEY,
    employee_first_name VARCHAR(100) NOT NULL,
    employee_last_name VARCHAR(100) NOT NULL,
    employee_start_date DATE NOT NULL,
    employee_end_date DATE,
    contact_number VARCHAR(20),
    position VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS customers (
    customer_id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    contact_number VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS drop_offs (
    drop_off_id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    drop_off_date DATE NOT NULL,
    notes TEXT,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE IF NOT EXISTS user_profile (
    uid BIGINT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    contact_number VARCHAR(255) NOT NULL,
    encrypted_password VARCHAR(255) NOT NULL,
    created_at TEXT,
    updated_at TEXT
);

-- Services
INSERT INTO services (service_id, service_name, service_description, service_price, fixed_rate)
VALUES (1, 'Destroy', '32-pass Data Erase; Followed with physical destruction.', 22.00, TRUE),
       (2, 'Re-Image', 'Reinstall the Operating System of a PC/Tablet/Laptop/Mobile device.', 50.00, TRUE),
       (3, 'Backup', 'Create an archived backup of all essential and user files.', 25.00, TRUE),
       (4, 'Diagnostic', 'Check the health of the physical Hardware, as well as scan for malicious software', 40.00, TRUE),
       (5, 'Hardware Upgrade', 'Install new Hardware and Drivers + Device Hardware health Scan.', 20.00, TRUE),
       (6, 'On-site Tech', 'On-Site technician, $99/first hr then $40/hr additional.', 99.00, FALSE);

-- Employees
INSERT INTO employees (employee_id, employee_first_name, employee_last_name, employee_start_date, employee_end_date,
                       contact_number, position)
VALUES ('emp001', 'Mike', 'Glabay', '2008-06-01', null, '555-0101', 'Owner'),
       ('emp002', 'Jane', 'Smith', '2023-02-01', null, '555-0102', 'Technician'),
       ('emp003', 'John', 'Doe', '2023-01-01', null, '555-0103', 'Technician'),
       ('emp004', 'Alice', 'Johnson', '2023-03-01', null, '555-0104', 'Manager');

-- Insert mock data into customers
INSERT INTO customers (customer_id, first_name, last_name, email, contact_number)
VALUES (1, 'John', 'Doe', 'john.doe@example.com', '555-0201'),
       (2, 'Sarah', 'Connor', 'sarah.connor@example.com', '555-0202'),
       (3, 'Bruce', 'Wayne', 'bruce.wayne@example.com', '555-0203');

-- Insert mock data into drop_offs
INSERT INTO drop_offs (drop_off_id, customer_id, drop_off_date, notes)
VALUES (1, 1, '2024-02-18', 'Customer requests express service.'),
       (2, 2, '2024-02-19', 'Screen replacement required.'),
       (3, 3, '2024-02-20', 'Battery replacement required.');
