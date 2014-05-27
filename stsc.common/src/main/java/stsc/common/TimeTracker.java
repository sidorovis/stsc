package stsc.common;

public final class TimeTracker {
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

	public double lengthInSeconds() {
		return lengthInSeconds(length());
	}

	public static double lengthInSeconds(long nanoseconds) {
		final long NANOSECONDS_IN_SECOND = 1000000000L;
		return Double.valueOf(nanoseconds) / NANOSECONDS_IN_SECOND;
	}

}