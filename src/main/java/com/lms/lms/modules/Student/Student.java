package com.lms.lms.modules.Student;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="student_id")
    private Long student_id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name="name_s")
    private String name_s;

    @NotNull
    @Size(min = 2, max = 50)
    @Column(name="surname")
    private String surname;

    private class TCValidator implements ConstraintValidator<TCConstraint, String> {
        @Override
        public boolean isValid(String tc, ConstraintValidatorContext context) {
            return tc != null && tc.length() == 11;
        }
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = TCValidator.class)
    private @interface TCConstraint {
        String message() default "Invalid tc.";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    @NotNull
    @TCConstraint
    @Column(name="tc")
    private String tc;

    @NotNull
    @Column(name="birth_date")
    private Date birth_date;

    @NotNull
    @Column(name="registration_date")
    private Date registration_date;

    @NotNull
    @Column(name="parent_name")
    private String parent_name;

    @NotNull
    @Column(name="parent_phone")
    private String parent_phone;

    @NotNull
    @Column(name="class_id")
    private String class_id; // TODO: Create classes entity then convert String to classes entity.

    // getters and setters
    public Long getId() { return student_id; }
    public void setId(Long student_id) { this.student_id = student_id; }

    public String getName() { return name_s; }
    public void setName(String name_s) { this.name_s = name_s; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getTC() { return tc; }
    public void setTC(String tc) { this.tc = tc; }

    public Date getBirthDate() { return birth_date; }
    public void setTC(Date birth_date) { this.birth_date = birth_date; }

    public Date getRegistrationDate() { return registration_date; }
    public void setRegistrationDate(Date registration_date) { this.registration_date = registration_date; }

    public String getParentName() { return parent_name; }
    public void setParentName(String parent_name) { this.parent_name = parent_name; }

    public String getParentPhone() { return parent_phone; }
    public void setParentPhone(String parent_phone) { this.parent_phone = parent_phone; }

    public String getClassId() { return class_id; } // TODO: Don't forget to change here also after creating classes entity.
    public void setClassId(String class_id) { this.class_id = class_id; }


}
