package stsc.common;

public class TimeTracker {
	private final long timeStart;
	private long timeEnd;

	public TimeTracker() {
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