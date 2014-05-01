package stsc.simulator.multistarter;

public enum ParameterType {
	integerType(0), doubleType(1), stringType(2), subExecutionType(3), size(4);
	public static int typesSize = 3;

	private int value;

	ParameterType(final int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
