CREATE TABLE favorites
(
    user_id    INT       NOT NULL,  -- FK to users table
    item_id    INT       NOT NULL,  -- FK to items table
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id, item_id), -- Ensures a user can only favorite an item once

    FOREIGN KEY fk_favorites_user (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY fk_favorites_item (item_id) REFERENCES items (id) ON DELETE CASCADE
);