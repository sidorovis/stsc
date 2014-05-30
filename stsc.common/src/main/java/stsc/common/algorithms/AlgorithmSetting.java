package stsc.common.algorithms;

public interface AlgorithmSetting<T extends Object> {

	public T getValue();

	public Class<T> getClassType();

	public void setInteger(final Integer newValue);
	public void setDouble(final Double newValue);
	public void setString(final String newValue);
	public void setValue(final T newValue);
}