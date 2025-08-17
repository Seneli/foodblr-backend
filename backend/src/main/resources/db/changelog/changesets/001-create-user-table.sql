--liquibase formatted sql

--changeset foodblr-dev:001-create-user-table
--comment: Create users table

CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    auth_provider VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

ALTER TABLE users ADD CONSTRAINT uk_users_email UNIQUE (email);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_auth_provider ON users (auth_provider);

--rollback DROP TABLE users; 