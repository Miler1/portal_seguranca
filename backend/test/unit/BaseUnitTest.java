package unit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.test.UnitTest;
import utils.PortalDeSegurancaExceptionMatcher;
import utils.ValidationExceptionMatcher;

import java.util.Collection;

public abstract class BaseUnitTest extends UnitTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void cleanValidationErros() {

		Validation.current().clear();

	}

	public void assertEmpty(Collection collection) {

		assertNotNull(collection);
		assertTrue(collection.isEmpty());

	}

	public void assertNotEmpty(Collection collection) {

		assertNotNull(collection);
		assertFalse(collection.isEmpty());

	}

	protected void assertValidationExceptionAndMessage(String messageKey, Object ... args) {

		expectedException.expect(ValidationExceptionMatcher.hasUserMessage(Messages.get(messageKey, args)));

	}

}