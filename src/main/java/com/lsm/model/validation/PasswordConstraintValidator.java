package com.lsm.model.validation;

import com.lsm.model.validation.constraint.PasswordConstraint;
import org.passay.*;
import org.springframework.stereotype.Component;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

@Component
public class PasswordConstraintValidator implements ConstraintValidator<PasswordConstraint, String> {

    private final PasswordValidator validator = new PasswordValidator(Arrays.asList(
            new LengthRule(8, 100),                     // Length between 8 and 100 characters
            new CharacterRule(EnglishCharacterData.UpperCase, 1), // At least 1 uppercase letter
            new CharacterRule(EnglishCharacterData.LowerCase, 1), // At least 1 lowercase letter
            new CharacterRule(EnglishCharacterData.Digit, 1),     // At least 1 digit
            new CharacterRule(EnglishCharacterData.Special, 1),   // At least 1 special character
            new WhitespaceRule()                       // No whitespace allowed
    ));

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        RuleResult result = validator.validate(new PasswordData(password));

        if (result.isValid()) {
            return true;
        }

        // Customize error messages based on rule violations
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(String.join(", ", validator.getMessages(result)))
                .addConstraintViolation();

        return false;
    }
}
