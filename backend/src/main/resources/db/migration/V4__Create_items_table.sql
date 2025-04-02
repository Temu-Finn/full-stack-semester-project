CREATE TABLE items
(
    id               INT                                                NOT NULL AUTO_INCREMENT PRIMARY KEY,
    seller_id        INT                                                NOT NULL, -- FK to users table (the seller)
    category_id      INT                                                NOT NULL, -- FK to categories table
    postal_code      VARCHAR(10)                                        NOT NULL, -- FK to postal_codes table
    title            VARCHAR(100)                                       NOT NULL,
    description      TEXT                                               NOT NULL,
    price            DECIMAL(10, 2)                                     NOT NULL,
    purchase_price   DECIMAL(10, 2)                                     NULL COMMENT 'Actual price sold for, if different from listing price and sold',
    buyer_id         INT                                                NULL, -- FK to users table (the buyer)
    location         POINT                                              NULL,
    allow_vipps_buy  BOOLEAN                                            NOT NULL DEFAULT FALSE,
    primary_image_id INT                                                NULL,     -- FK to item_images (main image). Constraint added via V6 migration.
    status           ENUM ('available', 'reserved', 'sold', 'archived') NOT NULL DEFAULT 'available' COMMENT 'Current status of the listing',
    created_at       TIMESTAMP                                          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP                                          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Indexes for performance when searching
    INDEX idx_items_seller_id (seller_id),
    INDEX idx_items_category_id (category_id),
    INDEX idx_items_postal_code (postal_code),
    INDEX idx_items_status (status),

    FOREIGN KEY fk_items_seller (seller_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY fk_items_category (category_id) REFERENCES categories (id) ON DELETE RESTRICT,
    FOREIGN KEY fk_items_postal_code (postal_code) REFERENCES postal_codes (postal_code) ON DELETE RESTRICT,
    FOREIGN KEY fk_items_buyer (buyer_id) REFERENCES users (id) ON DELETE SET NULL
    -- FK for primary_image_id added in V6
);