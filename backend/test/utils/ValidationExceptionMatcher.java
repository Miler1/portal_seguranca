package utils;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.rules.ExpectedException;

import exceptions.ValidationException;

public class ValidationExceptionMatcher extends TypeSafeMatcher<ValidationException> {

	private String expectedMessage, actualMessage;

	public static ValidationExceptionMatcher hasUserMessage(String item) {

		return new ValidationExceptionMatcher(item);

	}

	private ValidationExceptionMatcher(String expectedMessage) {

		this.expectedMessage = expectedMessage;

	}

	@Override
	public void describeTo(Description description) {

		description.appendValue(this.actualMessage)
				.appendText(" was not found instead of ")
				.appendValue(this.expectedMessage);

	}

	@Override
	protected boolean matchesSafely(ValidationException validationException) {

		ExpectedException.none().expect(ValidationException.class);

		this.actualMessage = validationException.getUserMessage();

		return this.actualMessage.indexOf(this.expectedMessage) >= 0;

	}

}