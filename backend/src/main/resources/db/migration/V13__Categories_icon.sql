ALTER TABLE categories
ADD COLUMN icon VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
DEFAULT '🚫' NOT NULL;

