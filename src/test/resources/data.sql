-- First drop dependent tables
DROP TABLE IF EXISTS student_submissions CASCADE;
DROP TABLE IF EXISTS class_courses CASCADE;
DROP TABLE IF EXISTS class_students CASCADE;
DROP TABLE IF EXISTS assignment_documents CASCADE;
DROP TABLE IF EXISTS teacher_classes CASCADE;
DROP TABLE IF EXISTS teacher_courses CASCADE;
DROP TABLE IF EXISTS attendance CASCADE;

-- Then drop main tables
DROP TABLE IF EXISTS assignments CASCADE;
DROP TABLE IF EXISTS classes CASCADE;
DROP TABLE IF EXISTS courses CASCADE;
DROP TABLE IF EXISTS app_users CASCADE;
DROP TABLE IF EXISTS refresh_tokens CASCADE;

-- Create tables in correct order
CREATE TABLE IF NOT EXISTS app_users (
                                         id BIGSERIAL PRIMARY KEY,
                                         username VARCHAR(255) NOT NULL UNIQUE,
                                         password VARCHAR(255) NOT NULL,
                                         name VARCHAR(255),
                                         surname VARCHAR(255),
                                         email VARCHAR(255) UNIQUE,
                                         role VARCHAR(50),
                                         enabled BOOLEAN DEFAULT TRUE,
                                         created_at TIMESTAMP,
                                         updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS classes (
                                       id BIGSERIAL PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
                                       description TEXT
);

CREATE TABLE IF NOT EXISTS courses (
                                       id BIGSERIAL PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
                                       description TEXT
);

CREATE TABLE IF NOT EXISTS assignments (
                                           id BIGSERIAL PRIMARY KEY,
                                           title VARCHAR(255) NOT NULL,
                                           description TEXT,
                                           due_date DATE,
                                           class_id BIGINT REFERENCES classes(id),
                                           course_id BIGINT REFERENCES courses(id),
                                           assigned_by_id BIGINT REFERENCES app_users(id),
                                           date DATE,
                                           last_modified DATE,
                                           last_modified_by_id BIGINT REFERENCES app_users(id)
);

CREATE TABLE IF NOT EXISTS assignment_documents (
                                                    id BIGSERIAL PRIMARY KEY,
                                                    assignment_id BIGINT REFERENCES assignments(id),
                                                    file_name VARCHAR(255),
                                                    file_path VARCHAR(255),
                                                    file_type VARCHAR(100),
                                                    file_size BIGINT,
                                                    upload_time TIMESTAMP,
                                                    uploaded_by_id BIGINT REFERENCES app_users(id)
);

CREATE TABLE IF NOT EXISTS student_submissions (
                                                   id BIGSERIAL PRIMARY KEY,
                                                   student_id BIGINT REFERENCES app_users(id),
                                                   assignment_id BIGINT REFERENCES assignments(id),
                                                   document_id BIGINT REFERENCES assignment_documents(id),
                                                   status VARCHAR(50),
                                                   grade INTEGER,
                                                   feedback TEXT,
                                                   submission_date TIMESTAMP
);