-- Fixtures for items in Trondheim
INSERT INTO db.items (seller_id, category_id, postal_code, title, description, price, purchase_price, buyer_id, location, allow_vipps_buy, primary_image_id, status, created_at, updated_at)
VALUES
    (1, 4, '7010', 'Burton Snowboard 🏂', 'Shred the slopes with style! Used Burton snowboard in great condition, ideal for winter adventures.', 2000.00, null, null, ST_SRID(POINT(63.4305,10.3951), 4326), 0, null, 'available', '2025-04-07 10:18:15', '2025-04-07 10:18:15'),
    (1, 1, '7011', 'Vintage Record Player', 'Well-maintained vintage record player perfect for collectors with original accessories.', 1500.00, null, null, ST_SRID(POINT(63.4306,10.3955), 4326), 1, null, 'available', DEFAULT, DEFAULT),
    (2, 2, '7012', 'Electric Mountain Bike', 'Lightly used electric mountain bike with a powerful motor, perfect for trail rides.', 12000.00, null, 1, ST_SRID(POINT(63.4280,10.3951), 4326), 1, null, 'reserved', DEFAULT, DEFAULT),
    (3, 1, '7013', 'High-end DSLR Camera', 'Professional DSLR camera with multiple lenses, ideal for photography enthusiasts.', 8000.00, 7200.00, 2, ST_SRID(POINT(63.4310,10.4050), 4326), 0, null, 'sold', DEFAULT, DEFAULT),
    (1, 1, '7014', 'Gaming Laptop', 'High-performance gaming laptop with the latest graphics, lightly used and powerful.', 18000.00, 16200.00, 3, ST_SRID(POINT(63.4320,10.3960), 4326), 0, null, 'sold', DEFAULT, DEFAULT),
    (2, 3, '7015', 'Acoustic Guitar', 'Well-maintained acoustic guitar with rich sound, suitable for beginners and pros.', 3000.00, null, null, ST_SRID(POINT(63.4290,10.3940), 4326), 1, null, 'available', DEFAULT, DEFAULT),
    (3, 1, '7016', 'Smartphone', 'Latest model smartphone in excellent condition, unlocked with accessories.', 7000.00, null, 1, ST_SRID(POINT(63.4330,10.3970), 4326), 0, null, 'reserved', DEFAULT, DEFAULT),
    (1, 8, '7017', 'Antique Vase', 'Beautiful antique vase with intricate designs, a perfect piece for collectors.', 2500.00, 2250.00, 2, ST_SRID(POINT(63.4340,10.3980), 4326), DEFAULT, null, 'sold', DEFAULT, DEFAULT),
    (2, 2, '7018', 'Road Bicycle', 'Lightweight road bicycle perfect for city commuting and leisure rides.', 5000.00, null, null, ST_SRID(POINT(63.4350,10.3990), 4326), DEFAULT, null, 'available', DEFAULT, DEFAULT),
    (3, 1, '7019', '4K Ultra HD TV', 'Large 4K Ultra HD television with smart features, ideal for home cinema.', 15000.00, null, 1, ST_SRID(POINT(63.4360,10.4000), 4326), DEFAULT, null, 'reserved', DEFAULT, DEFAULT),
    (1, 3, '7020', 'Digital Piano', 'Modern digital piano with weighted keys and multiple sound settings, gently used.', 9000.00, 8100.00, 2, ST_SRID(POINT(63.4370,10.4010), 4326), 1, null, 'sold', DEFAULT, DEFAULT),
    (2, 1, '7021', 'Wireless Headphones', 'Noise-cancelling wireless headphones with long battery life.', 2200.00, null, null, ST_SRID(POINT(63.4380,10.4020), 4326), 1, null, 'available', DEFAULT, DEFAULT),
    (3, 2, '7022', 'Treadmill', 'High-quality treadmill for home workouts with multiple speed settings.', 7000.00, null, 1, ST_SRID(POINT(63.4390,10.4030), 4326), DEFAULT, null, 'reserved', DEFAULT, DEFAULT),
    (1, 1, '7023', 'Bluetooth Speaker', 'Portable Bluetooth speaker with excellent sound quality for outdoor events.', 1800.00, null, null, ST_SRID(POINT(63.4400,10.4040), 4326), DEFAULT, null, 'available', DEFAULT, DEFAULT),
    (2, 4, '7024', 'Ski Set', 'Complete ski set including skis, boots, and poles for winter sports enthusiasts.', 3500.00, 3150.00, 3, ST_SRID(POINT(63.4410,10.4050), 4326), 1, null, 'sold', DEFAULT, DEFAULT),
    (3, 3, '7025', 'Electric Guitar', 'Sleek electric guitar with excellent sound quality, barely used.', 4000.00, null, null, ST_SRID(POINT(63.4420,10.4060), 4326), DEFAULT, null, 'available', DEFAULT, DEFAULT),
    (1, 1, '7026', 'Tablet', 'Lightweight tablet with high-resolution display, perfect for media and work.', 3000.00, null, 2, ST_SRID(POINT(63.4430,10.4070), 4326), DEFAULT, null, 'reserved', DEFAULT, DEFAULT),
    (2, 2, '7027', 'Camping Tent', 'Spacious camping tent, perfect for family trips and outdoor adventures.', 2500.00, null, null, ST_SRID(POINT(63.4440,10.4080), 4326), 1, null, 'available', DEFAULT, DEFAULT),
    (3, 8, '7028', 'Classic Watch', 'Elegant classic watch with leather strap, a timeless piece for any collection.', 5000.00, 4500.00, 1, ST_SRID(POINT(63.4450,10.4090), 4326), 1, null, 'sold', DEFAULT, DEFAULT),
    (1, 1, '7029', 'Smart Home Hub', 'Intuitive smart home hub to control all your smart devices.', 3500.00, null, null, ST_SRID(POINT(63.4460,10.4100), 4326), 1, null, 'available', DEFAULT, DEFAULT);

-- Elsewhere in Norway
INSERT INTO db.items (seller_id, category_id, postal_code, title, description, price, purchase_price, buyer_id, location, allow_vipps_buy, primary_image_id, status, created_at, updated_at)
VALUES
    (1, 1, '0150', 'Smartwatch Pro', 'Latest generation smartwatch with health tracking and long battery life.', 3500.00, null, null, ST_SRID(POINT(59.9139,10.7522), 4326), 1, null, 'available', DEFAULT, DEFAULT),
    (2, 2, '5003', 'Kayak', 'High-quality single-person kayak, perfect for both leisurely paddling and adventure trips.', 4500.00, null, 1, ST_SRID(POINT(60.3913,5.3221), 4326), DEFAULT, null, 'reserved', DEFAULT, DEFAULT),
    (3, 3, '4006', 'Electric Guitar', 'Sleek electric guitar with modern design, ideal for beginners and pros.', 5500.00, 4950.00, 2, ST_SRID(POINT(58.969975,5.733107), 4326), 1, null, 'sold', DEFAULT, DEFAULT),
    (1, 8, '9008', 'Antique Jewelry Box', 'Beautiful antique jewelry box with intricate carvings, a timeless collectible.', 2200.00, null, null, ST_SRID(POINT(69.6496,18.9560), 4326), DEFAULT, null, 'available', DEFAULT, DEFAULT),
    (2, 4, '4611', 'Ski Boots', 'Durable ski boots designed for performance and warmth on snowy days.', 3000.00, 2700.00, 3, ST_SRID(POINT(58.1467,7.9956), 4326), 1, null, 'sold', DEFAULT, DEFAULT);