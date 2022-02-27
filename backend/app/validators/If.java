package validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface If {

	enum IfOption {

		NULL,
		NOT_NULL,
		EMPTY,
		NOT_EMPTY,
		EQUALS,
		NOT_EQUALS,
		TRUE,
		FALSE,
		GREATER_THAN,
		LESS_TEHN

	}

	IfOption option();
	String attribute();
	String value() default "";

}