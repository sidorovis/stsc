package stsc.signals;

public class BadSignalException extends Exception {
	/**
	 * serial version UID for BiadSignalException
	 */
	private static final long serialVersionUID = -5868830080630854154L;

	public BadSignalException(String reason) {
		super(reason);
	}
}
