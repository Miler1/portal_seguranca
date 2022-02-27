package utils;

import exceptions.PortalSegurancaException;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.rules.ExpectedException;

public class PortalDeSegurancaExceptionMatcher extends TypeSafeMatcher<PortalSegurancaException> {

	private String expectedMessage, actualMessage;

	public static PortalDeSegurancaExceptionMatcher hasUserMessage(String item) {

		return new PortalDeSegurancaExceptionMatcher(item);

	}

	private PortalDeSegurancaExceptionMatcher(String expectedMessage) {

		this.expectedMessage = expectedMessage;

	}

	@Override
	public void describeTo(Description description) {

		description.appendValue(actualMessage)
				.appendText(" was not found instead of ")
				.appendValue(expectedMessage);

	}

	@Override
	protected boolean matchesSafely(PortalSegurancaException portalSegurancaException) {

		ExpectedException.none().expect(PortalSegurancaException.class);

		actualMessage = portalSegurancaException.getUserMessage();

		return actualMessage.indexOf(expectedMessage) >= 0;

	}

}