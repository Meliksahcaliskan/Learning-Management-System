-- Clear existing data and reset sequences
TRUNCATE TABLE class_courses, class_students, assignments, classes, courses, app_users CASCADE;

-- Reset sequences
ALTER SEQUENCE app_users_seq RESTART WITH 1;
ALTER SEQUENCE classes_id_seq RESTART WITH 1;
ALTER SEQUENCE assignments_seq RESTART WITH 1;
ALTER SEQUENCE courses_seq RESTART WITH 1;

-- Insert System Users (Admin & Coordinator)
INSERT INTO app_users (id, username, name, surname, email, password, role) VALUES
                                                                               (nextval('app_users_seq'), 'admin', 'Admin', 'User', 'admin@lms.com',
                                                                                '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_ADMIN'),
                                                                               (nextval('app_users_seq'), 'coordinator', 'Jane', 'Coordinator', 'coordinator@lms.com',
                                                                                '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_COORDINATOR');

-- Insert Teachers
INSERT INTO app_users (id, username, name, surname, email, password, role, teacher_phone, teacher_tc, teacher_birth_date) VALUES
                                                                                                                              (nextval('app_users_seq'), 'teacher1', 'John', 'Smith', 'john.smith@lms.com',
                                                                                                                               '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_TEACHER',
                                                                                                                               '5551112233', '12345678907', '1980-05-15'),
                                                                                                                              (nextval('app_users_seq'), 'teacher2', 'Mary', 'Johnson', 'mary.johnson@lms.com',
                                                                                                                               '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_TEACHER',
                                                                                                                               '5551112234', '12345678908', '1982-08-20');

-- Insert Students
INSERT INTO app_users (id, username, name, surname, email, password, role, student_phone, student_tc, student_birth_date,
                       student_registration_date, student_parent_name, student_parent_phone, class_id_student) VALUES
                                                                                                                   (nextval('app_users_seq'), 'student1', 'Alice', 'Brown', 'alice.brown@lms.com',
                                                                                                                    '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_STUDENT',
                                                                                                                    '5551234567', '12345678901', '2000-01-15', '2023-09-01', 'Robert Brown', '5551234568', 1),
                                                                                                                   (nextval('app_users_seq'), 'student2', 'Bob', 'Wilson', 'bob.wilson@lms.com',
                                                                                                                    '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_STUDENT',
                                                                                                                    '5552345678', '12345678902', '2001-03-20', '2023-09-01', 'Sarah Wilson', '5552345679', 1),
                                                                                                                   (nextval('app_users_seq'), 'student3', 'Charlie', 'Davis', 'charlie.davis@lms.com',
                                                                                                                    '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_STUDENT',
                                                                                                                    '5553456789', '12345678903', '2000-07-10', '2023-09-01', 'Michael Davis', '5553456780', 2),
                                                                                                                   (nextval('app_users_seq'), 'student4', 'Diana', 'Miller', 'diana.miller@lms.com',
                                                                                                                    '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_STUDENT',
                                                                                                                    '5554567890', '12345678904', '2001-11-05', '2023-09-01', 'James Miller', '5554567891', 2);

-- Insert Courses
INSERT INTO courses (id, name, description, code, credits) VALUES
                                                               (nextval('courses_seq'), 'Mathematics', 'Foundational mathematics course', 'MATH101', 4),
                                                               (nextval('courses_seq'), 'Physics', 'Introduction to physics', 'PHYS101', 4),
                                                               (nextval('courses_seq'), 'Chemistry', 'Basic chemistry concepts', 'CHEM101', 4);

-- Insert Classes
INSERT INTO classes (id, name, description, teacher_id) VALUES
                                                            (nextval('classes_id_seq'), 'Mathematics 101', 'Introduction to Mathematics',
                                                             (SELECT id FROM app_users WHERE username = 'teacher1')),
                                                            (nextval('classes_id_seq'), 'Physics 101', 'Introduction to Physics',
                                                             (SELECT id FROM app_users WHERE username = 'teacher2'));

-- Link Classes with Courses
INSERT INTO class_courses (class_id, course_id) VALUES
                                                    ((SELECT id FROM classes WHERE name = 'Mathematics 101'),
                                                     (SELECT id FROM courses WHERE code = 'MATH101')),
                                                    ((SELECT id FROM classes WHERE name = 'Physics 101'),
                                                     (SELECT id FROM courses WHERE code = 'PHYS101'));

-- Assign Students to Classes
INSERT INTO class_students (class_id, student_id)
SELECT c.id, s.id
FROM classes c, app_users s
WHERE s.role = 'ROLE_STUDENT';

-- Create Assignments
INSERT INTO assignments (id, title, description, due_date, assigned_by_teacher_id,
                         status, class_id, course_id, assignment_date) VALUES
                                                                           (nextval('assignments_seq'), 'Math Homework 1', 'Complete exercises 1-10', '2024-12-01',
                                                                            (SELECT id FROM app_users WHERE username = 'teacher1'), 'PENDING',
                                                                            (SELECT id FROM classes WHERE name = 'Mathematics 101'),
                                                                            (SELECT id FROM courses WHERE code = 'MATH101'), '2024-11-24'),
                                                                           (nextval('assignments_seq'), 'Physics Lab Report', 'Write report on gravity experiment', '2024-12-05',
                                                                            (SELECT id FROM app_users WHERE username = 'teacher2'), 'PENDING',
                                                                            (SELECT id FROM classes WHERE name = 'Physics 101'),
                                                                            (SELECT id FROM courses WHERE code = 'PHYS101'), '2024-11-24');

-- Update sequences to match current maximum values
SELECT setval('app_users_seq', (SELECT MAX(id) FROM app_users));
SELECT setval('classes_id_seq', (SELECT MAX(id) FROM classes));
SELECT setval('assignments_seq', (SELECT MAX(id) FROM assignments));
SELECT setval('courses_seq', (SELECT MAX(id) FROM courses));
