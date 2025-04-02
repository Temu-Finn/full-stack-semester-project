ALTER TABLE items
    ADD CONSTRAINT fk_items_primary_image
        FOREIGN KEY (primary_image_id) REFERENCES item_images (id)
            ON DELETE SET NULL -- If the specific primary image record is deleted, just nullify the FK link
            ON UPDATE CASCADE;

ALTER TABLE items
    ADD INDEX idx_items_primary_image_id (primary_image_id);