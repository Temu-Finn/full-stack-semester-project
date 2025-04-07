ALTER TABLE item_images
    ADD COLUMN image_data LONGBLOB;
ALTER TABLE item_images
    ADD COLUMN file_type VARCHAR(50);

ALTER TABLE item_images
DROP COLUMN image_url;
