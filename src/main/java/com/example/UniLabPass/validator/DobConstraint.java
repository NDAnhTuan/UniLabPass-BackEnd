package com.example.UniLabPass.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

// Annotation này sẽ được apply ở đâu
@Target({FIELD})
// Ano này sẽ được xử lý lúc nào
@Retention(RUNTIME)
// Class chịu trách nhiệm cho xử lý validator
@Constraint(validatedBy = {DobValidator.class})
public @interface DobConstraint {
    // 3 attributes basic of valid annotation
    String message() default "Invalid date of birth";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min();

}
