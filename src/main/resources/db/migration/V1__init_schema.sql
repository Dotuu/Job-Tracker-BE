CREATE TABLE tags (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     VARCHAR(255) NOT NULL,
    name        VARCHAR(50)  NOT NULL,
    color_hex   VARCHAR(7)   NOT NULL,
    scope       VARCHAR(20)  NOT NULL CHECK (scope IN ('CONTACT', 'APPLICATION')),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_tags_user_name_scope UNIQUE (user_id, name, scope)
) ENGINE=InnoDB;

CREATE TABLE resumes (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id        VARCHAR(255) NOT NULL,
    file_name      VARCHAR(255) NOT NULL,
    storage_path   VARCHAR(2048) NOT NULL,
    is_default     BOOLEAN NOT NULL DEFAULT FALSE,
    uploaded_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE job_applications (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         VARCHAR(255) NOT NULL,
    company_name    VARCHAR(255) NOT NULL,
    role_title      VARCHAR(255) NOT NULL,
    job_url         VARCHAR(2048),
    status          VARCHAR(30)  NOT NULL DEFAULT 'APPLIED',
    date_applied    DATE         NOT NULL,
    date_rejected   DATE,
    notes           TEXT,
    source          VARCHAR(100),
    resume_id       BIGINT,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_job_applications_resume FOREIGN KEY (resume_id) REFERENCES resumes(id)
) ENGINE=InnoDB;

CREATE TABLE contacts (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_application_id  BIGINT NOT NULL,
    name                VARCHAR(255) NOT NULL,
    email               VARCHAR(255),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_contacts_job_application FOREIGN KEY (job_application_id)
        REFERENCES job_applications(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE contact_tags (
    contact_id  BIGINT NOT NULL,
    tag_id      BIGINT NOT NULL,
    PRIMARY KEY (contact_id, tag_id),
    CONSTRAINT fk_contact_tags_contact FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE CASCADE,
    CONSTRAINT fk_contact_tags_tag     FOREIGN KEY (tag_id)     REFERENCES tags(id)     ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE job_application_tags (
    job_application_id  BIGINT NOT NULL,
    tag_id              BIGINT NOT NULL,
    PRIMARY KEY (job_application_id, tag_id),
    CONSTRAINT fk_job_application_tags_application FOREIGN KEY (job_application_id)
        REFERENCES job_applications(id) ON DELETE CASCADE,
    CONSTRAINT fk_job_application_tags_tag FOREIGN KEY (tag_id)
        REFERENCES tags(id) ON DELETE CASCADE
) ENGINE=InnoDB;
