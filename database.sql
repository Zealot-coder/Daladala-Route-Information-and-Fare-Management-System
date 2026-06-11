-- ============================================================
-- Daladala Route and Fare Information System
-- Database: daladala_db
-- Case Study: Dar es Salaam, Tanzania
-- ============================================================

CREATE DATABASE IF NOT EXISTS daladala_db;
USE daladala_db;

-- ============================================================
-- TABLE 1: admins
-- Stores admin login credentials.
-- No Spring Security — simple username/password check in code.
-- ============================================================
CREATE TABLE IF NOT EXISTS admins (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    full_name   VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLE 2: routes
-- Core table. One row = one daladala route.
-- fare_tzs: fare in Tanzanian Shillings (whole number, no decimals).
-- via_stops: comma-separated intermediate stops as plain text.
-- is_active: soft delete — inactive routes still appear in history.
-- ============================================================
CREATE TABLE IF NOT EXISTS routes (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_number VARCHAR(20)  NOT NULL UNIQUE,
    start_point  VARCHAR(100) NOT NULL,
    end_point    VARCHAR(100) NOT NULL,
    via_stops    VARCHAR(300),
    distance_km  DECIMAL(5,1) NOT NULL,
    fare_tzs     INT          NOT NULL,
    is_active    BOOLEAN      DEFAULT TRUE,
    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLE 3: route_history
-- Audit log. Written every time an admin creates, updates,
-- or deletes a route. Supports the "View route history" feature.
-- changed_by: stores admin username (not FK — keeps history
--             intact even if admin account is later deleted).
-- ============================================================
CREATE TABLE IF NOT EXISTS route_history (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_id           BIGINT       NOT NULL,
    action             VARCHAR(20)  NOT NULL,     -- CREATED | UPDATED | DELETED
    change_description VARCHAR(500) NOT NULL,
    changed_by         VARCHAR(50)  NOT NULL,
    changed_at         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_route FOREIGN KEY (route_id) REFERENCES routes(id)
);


-- ============================================================
-- SAMPLE DATA — admins
-- ============================================================
INSERT INTO admins (username, password, full_name) VALUES
('admin',   'admin123',   'System Administrator'),
('msimamizi', 'dar2024', 'Juma Msimamizi');


-- ============================================================
-- SAMPLE DATA — routes (18 realistic Dar es Salaam routes)
-- Fares reflect 2024 DART / daladala standard pricing in TZS.
-- ============================================================
INSERT INTO routes (route_number, start_point, end_point, via_stops, distance_km, fare_tzs, is_active) VALUES

-- Short city routes (500 – 700 TZS)
('DAL-05',  'Posta',     'Morocco',        'Kariakoo',                            6.0,   500, TRUE),
('DAL-08',  'Kariakoo',  'Kivukoni',       'Posta',                               4.0,   500, TRUE),
('DAL-11',  'Posta',     'Manzese',        'Kinondoni',                           7.0,   600, TRUE),
('DAL-20',  'Posta',     'Kigogo',         'Kariakoo',                            5.0,   500, TRUE),
('DAL-07',  'Kariakoo',  'Sinza',          'Magomeni',                            8.0,   700, TRUE),
('DAL-19',  'Posta',     'Tabata',         'Buguruni',                            9.0,   700, TRUE),
('DAL-32',  'Kariakoo',  'Changanyikeni',  'Magomeni',                            9.0,   700, TRUE),

-- Medium routes (800 – 1000 TZS)
('DAL-36',  'Kariakoo',  'Ubungo',         'Magomeni, Manzese',                  12.0,   800, TRUE),
('DAL-15',  'Kariakoo',  'Mwenge',         'Msasani',                            10.0,   800, TRUE),
('DAL-21',  'Kariakoo',  'Segerea',        'Buguruni',                           11.0,   800, TRUE),
('DAL-41',  'Kariakoo',  'Tandale',        'Magomeni',                           10.0,   800, TRUE),
('DAL-17',  'Kariakoo',  'Ukonga',         'Buguruni, Tabata',                   14.0,   900, TRUE),
('DAL-34',  'Kariakoo',  'Mbagala',        'Temeke',                             15.0,   900, TRUE),
('DAL-303', 'Posta',     'Kimara',         'Ubungo, Magomeni',                   18.0,  1000, TRUE),
('DAL-06',  'Kariakoo',  'Gongolamboto',   'Temeke',                             16.0,  1000, TRUE),
('DAL-13',  'Kariakoo',  'Kigamboni',      'Ferry Terminal',                     14.0,  1000, TRUE),
('DAL-24',  'Ubungo',    'Tegeta',         'Makumbusho, Mwenge',                 15.0,  1000, TRUE),

-- Long routes (1200 – 1500 TZS)
('DAL-16',  'Kariakoo',  'Tegeta',         'Mwenge, Makumbusho',                 22.0,  1200, TRUE),
('DAL-29',  'Kariakoo',  'Buza',           'Mbagala, Temeke',                    20.0,  1200, TRUE),
('DAL-45',  'Kariakoo',  'Mbezi Beach',    'Mwenge, Kimara',                     25.0,  1500, TRUE);


-- ============================================================
-- SAMPLE DATA — route_history (initial creation records)
-- ============================================================
INSERT INTO route_history (route_id, action, change_description, changed_by) VALUES
(1,  'CREATED', 'Route DAL-05 added: Posta to Morocco, fare 500 TZS',          'admin'),
(2,  'CREATED', 'Route DAL-08 added: Kariakoo to Kivukoni, fare 500 TZS',      'admin'),
(3,  'CREATED', 'Route DAL-11 added: Posta to Manzese, fare 600 TZS',          'admin'),
(13, 'CREATED', 'Route DAL-303 added: Posta to Kimara, fare 1000 TZS',         'admin'),
(17, 'UPDATED', 'Fare updated: DAL-16 from 1000 TZS to 1200 TZS (fuel cost)', 'msimamizi'),
(20, 'CREATED', 'Route DAL-45 added: Kariakoo to Mbezi Beach, fare 1500 TZS',  'admin');
