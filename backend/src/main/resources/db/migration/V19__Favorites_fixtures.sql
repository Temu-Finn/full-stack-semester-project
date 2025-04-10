-- Fixtures: Favorite items for user_id = 1 and user_id = 2
INSERT INTO db.favorites (user_id, item_id, created_at)
VALUES
    (1, 1, DEFAULT),   -- Burton Snowboard
    (1, 2, DEFAULT),   -- Vintage Record Player
    (1, 6, DEFAULT),   -- Acoustic Guitar
    (1, 13, DEFAULT),  -- Bluetooth Speaker
    (1, 19, DEFAULT),  -- Classic Watch
    (2, 3, DEFAULT),   -- Electric Mountain Bike
    (2, 5, DEFAULT),   -- Gaming Laptop
    (2, 10, DEFAULT),  -- 4K Ultra HD TV
    (2, 11, DEFAULT),  -- Digital Piano
    (2, 21, DEFAULT);  -- Kayak