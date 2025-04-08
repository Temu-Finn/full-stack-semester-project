ALTER TABLE categories
ADD COLUMN icon VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
DEFAULT '🚨' NOT NULL;

UPDATE categories SET icon = '📱' WHERE name = 'Electronics & Appliances';
UPDATE categories SET icon = '🪑' WHERE name = 'Furniture & Interior';
UPDATE categories SET icon = '👕' WHERE name = 'Clothing, Cosmetics & Accessories';
UPDATE categories SET icon = '🏀' WHERE name = 'Sports & Outdoor';
UPDATE categories SET icon = '🎮' WHERE name = 'Leisure, Hobby & Entertainment';
UPDATE categories SET icon = '🧸' WHERE name = 'Parents & Children';
UPDATE categories SET icon = '🚗' WHERE name = 'Vehicles & Equipment';
UPDATE categories SET icon = '🏡' WHERE name = 'Garden, Renovation & House';
