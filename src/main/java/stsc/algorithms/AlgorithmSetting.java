package stsc.algorithms;

public class AlgorithmSetting<T extends Object> {
	private T value;

	public AlgorithmSetting(final T defaultValue) {
		value = defaultValue;
	}

	public T getValue() {
		return value;
	}

	@SuppressWarnings("unchecked")
	public AlgorithmSetting<T> setValue(final Object newValue) {
		if (value.getClass() == newValue.getClass())
			value = (T) newValue;
		return this;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
