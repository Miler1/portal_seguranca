package exceptions;

import play.i18n.Messages;

public class PortalSegurancaException extends RuntimeException {

	private String messageKey;
	private Object [] messageArgs;

	public PortalSegurancaException () {

	}

	public PortalSegurancaException (Throwable throwable) {

		super(throwable);

	}

	public String getUserMessage () {

		return Messages.get(messageKey, messageArgs);

	}

	public PortalSegurancaException userMessage(String messageKey, Object ... messageArgs) {

		this.messageArgs = messageArgs;
		this.messageKey = messageKey;

		return this;

	}

}
