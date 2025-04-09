INSERT INTO db.messages (conversation_id, sender_id, content, sent_at, is_read) VALUES
(1, 101, 'Hi, is this item still available?', NOW() - INTERVAL 2 DAY, TRUE),
(1, 201, 'Yes, it is!', NOW() - INTERVAL 2 DAY + INTERVAL 1 HOUR, TRUE),
(1, 101, 'Great! Can we arrange a pickup?', NOW() - INTERVAL 1 DAY, FALSE);RVAL 1 DAY, FALSE);