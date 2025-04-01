CREATE TABLE postal_codes
(
    postal_code  VARCHAR(10)  NOT NULL PRIMARY KEY,
    postal_area  VARCHAR(100) NOT NULL COMMENT 'The common name for the postal code area (e.g., city/town)',
    municipality VARCHAR(100) NOT NULL,
    county       VARCHAR(100) NOT NULL
);