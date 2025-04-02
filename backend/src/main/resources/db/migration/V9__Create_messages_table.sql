CREATE TABLE messages
(
    id              INT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    conversation_id INT       NOT NULL, -- FK to the conversations table
    sender_id       INT       NOT NULL, -- FK to the users table
    content         TEXT      NOT NULL,
    sent_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read         BOOLEAN   NOT NULL DEFAULT FALSE,

    INDEX idx_messages_conversation_id (conversation_id),

    FOREIGN KEY fk_messages_conversation (conversation_id) REFERENCES conversations (id) ON DELETE CASCADE,
    FOREIGN KEY fk_messages_sender (sender_id) REFERENCES users (id) ON DELETE CASCADE
);