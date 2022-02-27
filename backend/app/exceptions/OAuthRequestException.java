package exceptions;

import play.data.validation.Error;

import java.util.List;

public class OAuthRequestException extends PortalSegurancaException {

	public List<Error> errors;

	public OAuthRequestException() {

		super();

	}

	public OAuthRequestException(List<Error> errors) {

		this.errors = errors;

		super.userMessage("erro", getErrorsMessage());

	}

	public OAuthRequestException(String motivoErro, Object ... args) {

		super.userMessage(motivoErro, args);

	}

	private String getErrorsMessage() {

		if (this.errors == null || this.errors.isEmpty()) {

			return super.getMessage();

		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < errors.size(); i++) {

			sb.append(errors.get(i).message()).append("\n");

		}

		return sb.toString();

	}

}
