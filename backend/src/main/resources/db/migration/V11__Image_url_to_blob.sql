ALTER TABLE item_images
ADD COLUMN image_blob LONGBLOB;

ALTER TABLE item_images
DROP COLUMN image_url;
