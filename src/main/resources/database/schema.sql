CREATE TABLE "roles" (
  "role_id" <type>,
  "role_name" <type>
);

CREATE INDEX "Key" ON  "roles" ("role_id", "role_name");

CREATE TABLE "app_users" (
  "id" BIGINT AUTO_INCREMENT PRIMARY KEY,
  "username" VARCHAR(60) NOT NULL UNIQUE,
  "email" VARCHAR(100) NOT NULL UNIQUE,
  "password" VARCHAR(100) NOT NULL,
  "role_id" VARCHAR(20) NOT NULL,
  CONSTRAINT "FK_app_users.id"
    FOREIGN KEY ("id")
      REFERENCES "roles"("role_id")
);

CREATE INDEX "Key" ON  "app_users" ("id", "username", "password", "role_id");

CREATE TABLE "remembered_users" (
  "id" <type>,
  "username" <type>,
  "password" <type>,
  "ip_address" <type>,
  CONSTRAINT "FK_remembered_users.id"
    FOREIGN KEY ("id")
      REFERENCES "app_users"("id")
);

CREATE INDEX "Key" ON  "remembered_users" ("id", "username", "password", "ip_address");

CREATE TABLE "students" (
  "student_id" <type>,
  "name_s" <type>,
  "surname" <type>,
  "tc" <type>,
  "birth_date" <type>,
  "registration_date" <type>,
  "parent_name" <type>,
  "parent_phone" <type>,
  "class_id" <type>
);

CREATE INDEX "Key" ON  "students" ("student_id", "name_s", "surname", "tc", "birth_date", "registration_date", "parent_name", "parent_phone", "class_id");

CREATE TABLE "attendance" (
  "attendance_id" <type>,
  "student_id" <type>,
  "date_a" <type>,
  "attendance" <type>,
  "comment" <type>,
  "class_id" <type>,
  CONSTRAINT "FK_attendance.student_id"
    FOREIGN KEY ("student_id")
      REFERENCES "students"("name_s"),
  CONSTRAINT "FK_attendance.comment"
    FOREIGN KEY ("comment")
      REFERENCES "students"("registration_date")
);

CREATE INDEX "Key" ON  "attendance" ("attendance_id", "student_id", "date_a", "attendance", "comment", "class_id");

CREATE TABLE "student_assignments" (
  "student_assignment_id" <type>,
  "student_id" <type>,
  "assignment_id" <type>,
  "status" <type>,
  "submission_date" <type>,
  "comment" <type>,
  CONSTRAINT "FK_student_assignments.student_id"
    FOREIGN KEY ("student_id")
      REFERENCES "students"("tc")
);

CREATE INDEX "Key" ON  "student_assignments" ("student_assignment_id", "student_id", "assignment_id", "status", "submission_date", "comment");

CREATE TABLE "classes" (
  "class_id" <type>,
  "class_name" <type>
);

CREATE INDEX "Key" ON  "classes" ("class_id", "class_name");

CREATE TABLE "lessons" (
  "lesson_id" <type>,
  "lesson_name" <type>
);

CREATE INDEX "Key" ON  "lessons" ("lesson_id", "lesson_name");

CREATE TABLE "class_lesson" (
  "class_lesson_id" <type>,
  "class_id" <type>,
  "lesson_id" <type>,
  CONSTRAINT "FK_class_lesson.class_lesson_id"
    FOREIGN KEY ("class_lesson_id")
      REFERENCES "classes"("class_id"),
  CONSTRAINT "FK_class_lesson.lesson_id"
    FOREIGN KEY ("lesson_id")
      REFERENCES "lessons"("lesson_id")
);

CREATE INDEX "Key" ON  "class_lesson" ("class_lesson_id", "class_id", "lesson_id");

CREATE TABLE "assignments" (
  "assignment_id" <type>,
  "class_id" <type>,
  "lesson_id" <type>,
  "description" <type>,
  "due_date" <type>,
  "creation_date" <type>,
  CONSTRAINT "FK_assignments.class_id"
    FOREIGN KEY ("class_id")
      REFERENCES "classes"("class_id"),
  CONSTRAINT "FK_assignments.description"
    FOREIGN KEY ("description")
      REFERENCES "lessons"("lesson_id")
);

CREATE INDEX "Key" ON  "assignments" ("assignment_id", "class_id", "lesson_id", "description", "due_date", "creation_date");


