CREATE TABLE status_history (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_application_id  BIGINT      NOT NULL,
    status              VARCHAR(30) NOT NULL,
    changed_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_status_history_application FOREIGN KEY (job_application_id)
        REFERENCES job_applications(id) ON DELETE CASCADE,
    INDEX idx_status_history_application (job_application_id, changed_at)
) ENGINE=InnoDB;