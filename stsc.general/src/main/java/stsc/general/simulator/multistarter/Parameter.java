package stsc.general.simulator.multistarter;

public class Parameter<T> {

	private final String name;
	private final T value;

	public Parameter(String name, T value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getStringValue() {
		return value.toString();
	}

	public T getValue() {
		return value;
	}

	@Override
	public String toString() {
		return name + " = " + value.toString();
	}

}
