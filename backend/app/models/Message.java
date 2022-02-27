package models;

import play.i18n.Messages;

public class Message<T> {

	private String text;

	private T data;

	public Message(String key, Object... args) {

		this.text = Messages.get(key, args);

	}

	public String getText() {

		return this.text;

	}

	public Object getData() {

		return data;

	}

	public Message withData(T data) {

		this.data = data;

		return this;

	}

	public Message setText(String key, Object... args) {

		this.text = Messages.get(key, args);

		return this;

	}

}