package com.lsm.model.validation.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.lsm.model.validation.TCValidator;
import com.nimbusds.jose.Payload;

import jakarta.validation.Constraint;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TCValidator.class)
public @interface TCConstraint {
    String message() default "Invalid TC number.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}