-- Clear existing data
DELETE FROM class_courses;
DELETE FROM class_students;
DELETE FROM assignments;
DELETE FROM classes;
DELETE FROM courses;
DELETE FROM app_users;

-- Reset sequences
ALTER SEQUENCE app_users_seq RESTART WITH 1;
ALTER SEQUENCE classes_id_seq RESTART WITH 1;
ALTER SEQUENCE assignments_seq RESTART WITH 1;
ALTER SEQUENCE courses_seq RESTART WITH 1;

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

-- Insert Courses
INSERT INTO courses (id, name, description, code, credits)
VALUES
    (nextval('courses_seq'), 'Mathematics', 'Foundational mathematics course', 'MATH101', 4),
    (nextval('courses_seq'), 'Physics', 'Introduction to physics', 'PHYS101', 4),
    (nextval('courses_seq'), 'Chemistry', 'Basic chemistry concepts', 'CHEM101', 4);

-- Insert Classes
INSERT INTO classes (id, name, description, teacher_id)
VALUES
    (nextval('classes_id_seq'), 'Mathematics 101', 'Introduction to Mathematics',
     (SELECT id FROM app_users WHERE username = 'teacher1')),
    (nextval('classes_id_seq'), 'Physics 101', 'Introduction to Physics',
     (SELECT id FROM app_users WHERE username = 'teacher2'));

-- Insert Class-Course relationships
INSERT INTO class_courses (class_id, course_id)
VALUES
    ((SELECT id FROM classes WHERE name = 'Mathematics 101'),
     (SELECT id FROM courses WHERE code = 'MATH101')),
    ((SELECT id FROM classes WHERE name = 'Physics 101'),
     (SELECT id FROM courses WHERE code = 'PHYS101'));

-- Insert Class-Student relationships
INSERT INTO class_students (class_id, student_id)
VALUES
    ((SELECT id FROM classes WHERE name = 'Mathematics 101'),
     (SELECT id FROM app_users WHERE username = 'student1')),
    ((SELECT id FROM classes WHERE name = 'Physics 101'),
     (SELECT id FROM app_users WHERE username = 'student1')),
-- [Rest of the class_students INSERT statements]

-- Insert Assignments
INSERT INTO assignments (
    id, title, description, due_date, assigned_by_teacher_id,
    status, class_id, course_id, assignment_date
)
VALUES
    (nextval('assignments_seq'), 'Math Homework 1', 'Complete exercises 1-10', '2024-12-01',
    (SELECT id FROM app_users WHERE username = 'teacher1'),
    'PENDING',
    (SELECT id FROM classes WHERE name = 'Mathematics 101'),
    (SELECT id FROM courses WHERE code = 'MATH101'),
    '2024-11-24'),

    (nextval('assignments_seq'), 'Physics Lab Report', 'Write report on gravity experiment', '2024-12-05',
    (SELECT id FROM app_users WHERE username = 'teacher2'),
    'PENDING',
    (SELECT id FROM classes WHERE name = 'Physics 101'),
    (SELECT id FROM courses WHERE code = 'PHYS101'),
    '2024-11-24');

-- Update sequences to next values
SELECT setval('app_users_seq', (SELECT MAX(id) FROM app_users));
SELECT setval('classes_id_seq', (SELECT MAX(id) FROM classes));
SELECT setval('assignments_seq', (SELECT MAX(id) FROM assignments));
SELECT setval('courses_seq', (SELECT MAX(id) FROM courses));