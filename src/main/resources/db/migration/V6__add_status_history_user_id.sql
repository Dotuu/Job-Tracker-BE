ALTER TABLE status_history
    ADD COLUMN user_id VARCHAR(255) NOT NULL AFTER id,
    ADD INDEX idx_status_history_user (user_id);
