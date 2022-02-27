package validators;

import net.sf.oval.configuration.annotation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(checkWith = UniqueIfNotRemovedCheck.class)
public @interface UniqueIfNotRemoved {

	String message() default UniqueIfNotRemovedCheck.message;

}