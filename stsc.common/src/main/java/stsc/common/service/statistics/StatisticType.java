package stsc.common.service.statistics;

public enum StatisticType {
// @formatter:off
	ALL(0), 
	TRACE(10), 
	TRACE_INT(20), 
	DEBUG(30), 
	INFO(40), 
	WARN(50),
	ERROR(60), 
	FATAL(70), 
	OFF(80);
// @formatter:on

	private final int value;

	private StatisticType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
