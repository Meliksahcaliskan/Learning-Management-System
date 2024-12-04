-- Clear existing data and reset sequences
TRUNCATE TABLE class_courses, class_students, assignments, classes, courses, app_users,
           refresh_tokens, assignment_documents, teacher_classes, teacher_courses CASCADE;

-- Reset sequences
ALTER SEQUENCE app_users_seq RESTART WITH 1;
ALTER SEQUENCE classes_id_seq RESTART WITH 1;
ALTER SEQUENCE assignments_seq RESTART WITH 1;
ALTER SEQUENCE courses_seq RESTART WITH 1;
ALTER SEQUENCE refresh_token_seq RESTART WITH 1;
ALTER SEQUENCE assignment_docs_seq RESTART WITH 1;

-- Insert System Users (Admin & Coordinator)
INSERT INTO app_users (id, username, name, surname, email, password, role) VALUES
                                                                               (nextval('app_users_seq'), 'admin', 'Admin', 'User', 'admin@lms.com',
                                                                                '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_ADMIN'),
                                                                               (nextval('app_users_seq'), 'coordinator', 'Jane', 'Coordinator', 'coordinator@lms.com',
                                                                                '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_COORDINATOR');

-- Insert Teachers with TeacherDetails
INSERT INTO app_users (id, username, name, surname, email, password, role,
                       teacher_phone, teacher_tc, teacher_birth_date) VALUES
                                                                          (nextval('app_users_seq'), 'teacher1', 'John', 'Smith', 'john.smith@lms.com',
                                                                           '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_TEACHER',
                                                                           '5551112233', '12345678907', '1980-05-15'),
                                                                          (nextval('app_users_seq'), 'teacher2', 'Mary', 'Johnson', 'mary.johnson@lms.com',
                                                                           '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_TEACHER',
                                                                           '5551112234', '12345678908', '1982-08-20');

-- Insert Students with StudentDetails
INSERT INTO app_users (id, username, name, surname, email, password, role,
                       student_phone, student_tc, student_birth_date, student_registration_date,
                       student_parent_name, student_parent_phone, class_id_student) VALUES
                                                                                        (nextval('app_users_seq'), 'student1', 'Alice', 'Brown', 'alice.brown@lms.com',
                                                                                         '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_STUDENT',
                                                                                         '5551234567', '12345678901', '2008-01-15', '2023-09-01', 'Robert Brown', '5551234568', 1),
                                                                                        (nextval('app_users_seq'), 'student2', 'Bob', 'Wilson', 'bob.wilson@lms.com',
                                                                                         '$2a$12$KI8ugVXiXKu6Q7VthcY2u.JGVmh0OQ6wtx6NnK31G1TnGbEbSTgzG', 'ROLE_STUDENT',
                                                                                         '5552345678', '12345678902', '2007-03-20', '2023-09-01', 'Sarah Wilson', '5552345679', 1);

-- Insert Courses
INSERT INTO courses (id, name, description, code, credits) VALUES
                                                               (nextval('courses_seq'), 'AYT-Mathematics', 'Foundational mathematics course', 'MAT-1', 4),
                                                               (nextval('courses_seq'), 'AYT-Physics', 'Introduction to physics', 'FIZ-1', 4),
                                                               (nextval('courses_seq'), 'AYT-Literacy', 'Basic turkish literacy concepts', 'EDB-1', 4);

-- Insert Classes
INSERT INTO classes (id, name, description, teacher_id) VALUES
                                                            (nextval('classes_id_seq'), '11-A-MF', '11-A MF',
                                                             (SELECT id FROM app_users WHERE username = 'teacher1')),
                                                            (nextval('classes_id_seq'), '11-B-TM', '11-B TM',
                                                             (SELECT id FROM app_users WHERE username = 'teacher2'));

-- Link Classes with Courses
INSERT INTO class_courses (class_id, course_id) VALUES
                                                    ((SELECT id FROM classes WHERE name = '11-A-MF'),
                                                     (SELECT id FROM courses WHERE code = 'MAT-1')),
                                                    ((SELECT id FROM classes WHERE name = '11-B-TM'),
                                                     (SELECT id FROM courses WHERE code = 'FIZ-1'));

-- Insert Teacher-Course Relationships
INSERT INTO teacher_courses (user_id, course_id) VALUES
                                                     ((SELECT id FROM app_users WHERE username = 'teacher1'),
                                                      (SELECT id FROM courses WHERE code = 'MAT-1')),
                                                     ((SELECT id FROM app_users WHERE username = 'teacher2'),
                                                      (SELECT id FROM courses WHERE code = 'EDB-1'));

-- Insert Teacher-Class Relationships
INSERT INTO teacher_classes (user_id, class_id) VALUES
                                                    ((SELECT id FROM app_users WHERE username = 'teacher1'),
                                                     (SELECT id FROM classes WHERE name = '11-A-MF')),
                                                    ((SELECT id FROM app_users WHERE username = 'teacher2'),
                                                     (SELECT id FROM classes WHERE name = '11-B-TM'));

-- Assign Students to Classes
INSERT INTO class_students (class_id, student_id)
SELECT c.id, s.id
FROM classes c, app_users s
WHERE s.role = 'ROLE_STUDENT';

-- Create Assignments
INSERT INTO assignments (id, title, description, due_date, submission_date, assigned_by_teacher_id,
                         status, class_id, course_id, assignment_date) VALUES
                                                                           (nextval('assignments_seq'), 'Math Homework 1', 'Complete exercises 1-10', '2024-12-01', null,
                                                                            (SELECT id FROM app_users WHERE username = 'teacher1'), 'PENDING',
                                                                            (SELECT id FROM classes WHERE name = '11-A-MF'),
                                                                            (SELECT id FROM courses WHERE code = 'MAT-1'), '2024-11-24'),
                                                                           (nextval('assignments_seq'), 'Literacy Report', 'Write report on turkish literacy', '2024-12-05', null,
                                                                            (SELECT id FROM app_users WHERE username = 'teacher2'), 'PENDING',
                                                                            (SELECT id FROM classes WHERE name = '11-B-TM'),
                                                                            (SELECT id FROM courses WHERE code = 'EDB-1'), '2024-11-24');

-- Create Assignment Documents
INSERT INTO assignment_documents (id, file_name, file_path, upload_time, file_type, file_size,
                                  uploaded_by, is_teacher_upload, assignment_id) VALUES
                                                                                     (nextval('assignment_docs_seq'),
                                                                                      'math_homework.pdf',
                                                                                      'assignments/math/math_homework.pdf',
                                                                                      '2024-11-24 09:00:00',
                                                                                      'application/pdf',
                                                                                      399516,
                                                                                      (SELECT id FROM app_users WHERE username = 'teacher1'),
                                                                                      true,
                                                                                      (SELECT id FROM assignments WHERE title = 'Math Homework 1')),
                                                                                     (nextval('assignment_docs_seq'),
                                                                                      'literature_hw.pdf',
                                                                                      'assignments/literature/literature_hw.pdf',
                                                                                      '2024-11-24 10:00:00',
                                                                                      'application/pdf',
                                                                                      591446,
                                                                                      (SELECT id FROM app_users WHERE username = 'teacher2'),
                                                                                      true,
                                                                                      (SELECT id FROM assignments WHERE title = 'Literacy Report'));

-- Update sequences
SELECT setval('app_users_seq', (SELECT MAX(id) FROM app_users));
SELECT setval('classes_id_seq', (SELECT MAX(id) FROM classes));
SELECT setval('assignments_seq', (SELECT MAX(id) FROM assignments));
SELECT setval('courses_seq', (SELECT MAX(id) FROM courses));
SELECT setval('refresh_token_seq', (SELECT MAX(id) FROM refresh_tokens));
SELECT setval('assignment_docs_seq', (SELECT MAX(id) FROM assignment_documents));