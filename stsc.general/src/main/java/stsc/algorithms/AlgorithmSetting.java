package stsc.algorithms;

public final class AlgorithmSetting<T extends Object> {
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

	@SuppressWarnings("unchecked")
	void setInteger(final Integer newValue) {
		value = (T) newValue;
	}

	@SuppressWarnings("unchecked")
	void setDouble(final Double newValue) {
		value = (T) newValue;
	}

	@SuppressWarnings("unchecked")
	void setString(final String newValue) {
		value = (T) newValue;
	}

	void setValue(final T newValue) {
		value = newValue;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	public Class<T> getClassType() {
		return clazz;
	}
}
