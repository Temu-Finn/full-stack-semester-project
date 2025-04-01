CREATE TABLE item_images
(
    id          INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    item_id     INT          NOT NULL, -- FK to the items table
    image_url   VARCHAR(512) NOT NULL,
    alt_text    VARCHAR(255) NULL, -- Remember to coalesce this in repository code
    uploaded_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_item_images_item_id (item_id),

    FOREIGN KEY fk_item_images_item (item_id) REFERENCES items (id) ON DELETE CASCADE
);