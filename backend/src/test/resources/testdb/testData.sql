-- V1
DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    user_id    INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    name       VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    is_admin   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- V2
DROP TABLE IF EXISTS categories;
CREATE TABLE categories
(
    id          INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(30)  NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- V3
DROP TABLE IF EXISTS postal_codes;
CREATE TABLE postal_codes
(
    postal_code  VARCHAR(10)  NOT NULL PRIMARY KEY,
    city VARCHAR(100) NOT NULL COMMENT 'The postal code area (e.g., poststed)',
    municipality VARCHAR(100) NOT NULL,
    county       VARCHAR(100) NOT NULL
);

-- V4

CREATE TABLE items
(
    id               INT                                                NOT NULL AUTO_INCREMENT PRIMARY KEY,
    seller_id        INT                                                NOT NULL,
    category_id      INT                                                NOT NULL,
    postal_code      VARCHAR(10)                                        NOT NULL,
    title            VARCHAR(100)                                       NOT NULL,
    description      TEXT                                               NOT NULL,
    price            DECIMAL(10, 2)                                     NOT NULL,
    purchase_price   DECIMAL(10, 2)                                     NULL,
    buyer_id         INT                                                NULL,
    location         POINT                                              NULL,
    allow_vipps_buy  BOOLEAN                                            NOT NULL DEFAULT FALSE,
    primary_image_id INT                                                NULL,
    status           ENUM ('available', 'reserved', 'sold', 'archived') NOT NULL DEFAULT 'available',
    created_at       TIMESTAMP                                          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP                                          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_items_seller_id (seller_id),
    INDEX idx_items_category_id (category_id),
    INDEX idx_items_postal_code (postal_code),
    INDEX idx_items_status (status),

    FOREIGN KEY fk_items_seller (seller_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY fk_items_category (category_id) REFERENCES categories (category_id) ON DELETE RESTRICT,
    FOREIGN KEY fk_items_postal_code (postal_code) REFERENCES postal_codes (postal_code) ON DELETE RESTRICT,
    FOREIGN KEY fk_items_buyer (buyer_id) REFERENCES users (user_id) ON DELETE SET NULL
);