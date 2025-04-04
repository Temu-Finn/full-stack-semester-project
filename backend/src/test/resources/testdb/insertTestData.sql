-- Insert test user
INSERT INTO users (name, email, password, is_admin)
VALUES ('Test Seller', 'seller@example.com', '123', false),
       ('Test Buyer', 'buyer@example.com', '456', false);

-- Insert test category
INSERT INTO categories (name, description)
VALUES ('ELECTRONICS', 'Test category for electronics');

-- Insert test postal code
INSERT INTO postal_codes (postal_code, city, municipality, county)
VALUES (7014, 'Trondheim', 'idk', 'idk');