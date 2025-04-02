CREATE TABLE conversations
(
    id         INT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    item_id   INT NOT NULL,                         -- FK to the item being discussed
    buyer_id  INT NOT NULL,                         -- FK to the user table (potential buyer)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of the last message or activity in the conversation',

    UNIQUE KEY uk_conversation (item_id, buyer_id), -- Ensures only one conversation thread per buyer per item

    FOREIGN KEY fk_conversations_item (item_id) REFERENCES items (id) ON DELETE CASCADE,
    FOREIGN KEY fk_conversations_buyer (buyer_id) REFERENCES users (id) ON DELETE CASCADE
);