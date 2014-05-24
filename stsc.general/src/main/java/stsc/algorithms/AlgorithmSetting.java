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

	@SuppressWarnings("unchecked")
	public void setInteger(final Integer newValue) {
		value = (T) newValue;
	}

	@SuppressWarnings("unchecked")
	public void setDouble(final Double newValue) {
		value = (T) newValue;
	}

	@SuppressWarnings("unchecked")
	public void setString(final String newValue) {
		value = (T) newValue;
	}

	public void setValue(final T newValue) {
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
