CREATE TABLE items
(
    item_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    item_owner INTEGER NOT NULL,
    item_category INTEGER NOT NULL,
    item_name VARCHAR(255) NOT NULL,
    item_price DOUBLE NOT NULL,
    item_description TEXT,
    is_sold BOOLEAN DEFAULT FALSE,
    item_condition ENUM('NEW', 'LIKE_NEW', 'USED', 'WELL_USED', 'DAMAGED') DEFAULT USED,

    FOREIGN KEY (item_owner) REFERENCES users(user_id),
    FOREIGN KEY (item_category) REFERENCES categories(category_id)
);