CREATE TABLE users
(
    user_id   INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
