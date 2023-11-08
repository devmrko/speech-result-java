package com.oracle.fn;

public class InputParseException extends Exception {

	private static final long serialVersionUID = -5375556111049611117L;

	public InputParseException() {
		super();
	}

	public InputParseException(String message) {
		super(message);
	}

	public InputParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public InputParseException(Throwable cause) {
		super(cause);
	}
}