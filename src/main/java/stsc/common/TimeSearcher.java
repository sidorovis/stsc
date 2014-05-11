package stsc.common;

public class TimeSearcher {
	private final long timeStart;
	private long timeEnd;

	public TimeSearcher() {
		timeStart = System.nanoTime();
	}

	public long finish() {
		timeEnd = System.nanoTime();
		return length();
	}

	public long length() {
		return timeEnd - timeStart;
	}
}