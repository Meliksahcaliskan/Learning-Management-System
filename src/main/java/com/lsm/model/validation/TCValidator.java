package com.lsm.model.validation;

import com.lsm.model.validation.constraint.TCConstraint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TCValidator implements ConstraintValidator<TCConstraint, String> {
    @Override
    public void initialize(TCConstraint constraintAnnotation) {
        // Initialization code if needed
    }

    @Override
    public boolean isValid (String tc, ConstraintValidatorContext context) {
        if (tc == null || tc.length() != 11 || tc.charAt(0) == '0') {
            return false;
        }

        int oddSum = 0, evenSum = 0, controlDigit;

        for (int i = 0; i <= 8; i++) {
            int digit = Character.getNumericValue(tc.charAt(i));
            if (i % 2 == 0) {
                oddSum += digit;
            } else {
                evenSum += digit;
            }
        }

        controlDigit = (oddSum * 7 - evenSum) % 10;
        if (Character.getNumericValue(tc.charAt(9)) != controlDigit) {
            return false;
        }

        int totalSum = oddSum + evenSum + controlDigit;
        return Character.getNumericValue(tc.charAt(10)) == totalSum % 10;
    }
}
