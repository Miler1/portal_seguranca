package validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(checkWith = RequiredIfCheck.class)
public @interface RequiredIf {

	enum Operator {

		AND,
		OR

	}

	If[] ifs();
	Operator operator() default Operator.AND;
	String message() default RequiredIfCheck.message;

}