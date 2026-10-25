ALTER TABLE job_applications
    DROP FOREIGN KEY fk_job_applications_resume;

ALTER TABLE job_applications
    ADD CONSTRAINT fk_job_applications_resume
        FOREIGN KEY (resume_id) REFERENCES resumes(id)
        ON DELETE SET NULL;

ALTER TABLE resumes
    MODIFY COLUMN storage_path VARCHAR(100) NOT NULL,
    ADD COLUMN content_type VARCHAR(100) NOT NULL AFTER storage_path,
    ADD COLUMN size_bytes   BIGINT       NOT NULL AFTER content_type,
    ADD COLUMN sha256       CHAR(64)     NULL     AFTER size_bytes,
    ADD CONSTRAINT uq_resumes_storage_path UNIQUE (storage_path),
    ADD CONSTRAINT chk_resumes_size_bytes CHECK (size_bytes > 0);

CREATE INDEX idx_resumes_user_id ON resumes (user_id);
