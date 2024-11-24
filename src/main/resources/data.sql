-- Clear existing data
DELETE FROM user_classes;
DELETE FROM app_users;

-- Reset sequence
ALTER SEQUENCE app_users_seq RESTART WITH 1;

-- [Previous INSERT statements remain the same but using the correct sequence]
-- Insert Admin
INSERT INTO app_users (id, username, name, surname, email, password, role)
VALUES (nextval('app_users_seq'), 'admin', 'Admin', 'User', 'admin@lms.com',
        '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_ADMIN');

-- Insert Coordinator
INSERT INTO app_users (id, username, name, surname, email, password, role)
VALUES (nextval('app_users_seq'), 'coordinator', 'Jane', 'Coordinator', 'coordinator@lms.com',
        '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_COORDINATOR');

-- Insert Teachers
INSERT INTO app_users (id, username, name, surname, email, password, role)
VALUES (nextval('app_users_seq'), 'teacher1', 'John', 'Smith', 'john.smith@lms.com',
        '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_TEACHER');

INSERT INTO app_users (id, username, name, surname, email, password, role)
VALUES (nextval('app_users_seq'), 'teacher2', 'Mary', 'Johnson', 'mary.johnson@lms.com',
        '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_TEACHER');

-- Insert Students with StudentDetails
INSERT INTO app_users (id, username, name, surname, email, password, role,
                       phone, tc, birth_date, registration_date, parent_name, parent_phone)
VALUES (nextval('app_users_seq'), 'student1', 'Alice', 'Brown', 'alice.brown@lms.com',
        '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_STUDENT',
        '5551234567', '12345678901', '2000-01-15', '2023-09-01', 'Robert Brown', '5551234568');

INSERT INTO app_users (id, username, name, surname, email, password, role,
                       phone, tc, birth_date, registration_date, parent_name, parent_phone)
VALUES (nextval('app_users_seq'), 'student2', 'Bob', 'Wilson', 'bob.wilson@lms.com',
        '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_STUDENT',
        '5552345678', '12345678902', '2001-03-20', '2023-09-01', 'Sarah Wilson', '5552345679');

INSERT INTO app_users (id, username, name, surname, email, password, role,
                       phone, tc, birth_date, registration_date, parent_name, parent_phone)
VALUES (nextval('app_users_seq'), 'student3', 'Charlie', 'Davis', 'charlie.davis@lms.com',
        '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_STUDENT',
        '5553456789', '12345678903', '2000-07-10', '2023-09-01', 'Michael Davis', '5553456780');

INSERT INTO app_users (id, username, name, surname, email, password, role,
                       phone, tc, birth_date, registration_date, parent_name, parent_phone)
VALUES (nextval('app_users_seq'), 'student4', 'Diana', 'Miller', 'diana.miller@lms.com',
        '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_STUDENT',
        '5554567890', '12345678904', '2001-11-05', '2023-09-01', 'James Miller', '5554567891');

INSERT INTO app_users (id, username, name, surname, email, password, role,
                       phone, tc, birth_date, registration_date, parent_name, parent_phone)
VALUES (nextval('app_users_seq'), 'student5', 'Eva', 'Taylor', 'eva.taylor@lms.com',
        '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_STUDENT',
        '5555678901', '12345678905', '2000-09-25', '2023-09-01', 'William Taylor', '5555678902');

INSERT INTO app_users (id, username, name, surname, email, password, role,
                       phone, tc, birth_date, registration_date, parent_name, parent_phone)
VALUES (nextval('app_users_seq'), 'student6', 'Frank', 'Anderson', 'frank.anderson@lms.com',
        '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_STUDENT',
        '5556789012', '12345678906', '2001-05-30', '2023-09-01', 'Patricia Anderson', '5556789013');

-- Insert sample class assignments for students
INSERT INTO user_classes (user_id, class_id) VALUES
                                                 ((SELECT id FROM app_users WHERE username = 'student1'), 1),
                                                 ((SELECT id FROM app_users WHERE username = 'student1'), 2),
                                                 ((SELECT id FROM app_users WHERE username = 'student2'), 1),
                                                 ((SELECT id FROM app_users WHERE username = 'student3'), 2),
                                                 ((SELECT id FROM app_users WHERE username = 'student4'), 1),
                                                 ((SELECT id FROM app_users WHERE username = 'student5'), 2),
                                                 ((SELECT id FROM app_users WHERE username = 'student6'), 1);

-- Update sequence to the next value
SELECT setval('app_users_seq', (SELECT MAX(id) FROM app_users));