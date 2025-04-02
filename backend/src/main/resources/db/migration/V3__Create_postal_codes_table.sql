CREATE TABLE postal_codes
(
    postal_code  VARCHAR(10)  NOT NULL PRIMARY KEY,
    city VARCHAR(100) NOT NULL COMMENT 'The postal code area (e.g., poststed)',
    municipality VARCHAR(100) NOT NULL,
    county       VARCHAR(100) NOT NULL
);
