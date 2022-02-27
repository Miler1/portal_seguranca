package exceptions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import play.data.validation.Error;

public class ValidationException extends PortalSegurancaException {

	public List<Error> errors;

	public ValidationException() {

		super();

	}

	public ValidationException(List<Error> errors) {

		this.errors = errors;

		super.userMessage("erro", getErrorsMessage());

	}

	public ValidationException(String motivoErro, Object ... args) {

		super.userMessage(motivoErro, args);

	}

	private String getErrorsMessage() {

		if(this.errors == null || this.errors.isEmpty()) {

			return super.getMessage();

		}

		Set<String> errorsMessage = new HashSet<>();

		for(Error error : errors) {

			errorsMessage.add(error.message());

		}

		StringBuilder sb = new StringBuilder();

		for(String s : errorsMessage) {

			sb.append(s).append("\n");

		}

		return sb.toString();

	}

}
