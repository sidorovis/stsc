package stsc.algorithms;

public class AlgorithmSetting<T extends Object> {
	private T value;
	final private Class<T> clazz;

	@SuppressWarnings("unchecked")
	public AlgorithmSetting(final T defaultValue) {
		value = defaultValue;
		clazz = (Class<T>) defaultValue.getClass();
	}

	public T getValue() {
		return value;
	}

	public AlgorithmSetting<T> setValue(final T newValue) {
		value = newValue;
		return this;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	public Class<T> getClassType() {
		return clazz;
	}
}
