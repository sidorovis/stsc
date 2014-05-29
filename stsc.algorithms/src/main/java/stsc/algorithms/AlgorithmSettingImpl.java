package stsc.algorithms;

import stsc.common.algorithms.AlgorithmSetting;

public final class AlgorithmSettingImpl<T> implements AlgorithmSetting<T> {
	private T value;
	final private Class<T> clazz;

	@SuppressWarnings("unchecked")
	public AlgorithmSettingImpl(final T defaultValue) {
		value = defaultValue;
		clazz = (Class<T>) defaultValue.getClass();
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setInteger(final Integer newValue) {
		value = (T) newValue;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setDouble(final Double newValue) {
		value = (T) newValue;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setString(final String newValue) {
		value = (T) newValue;
	}

	@Override
	public void setValue(final T newValue) {
		value = newValue;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public Class<T> getClassType() {
		return clazz;
	}
}
