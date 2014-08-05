package stsc.common.algorithms;

import java.util.List;

import stsc.common.FromToPeriod;

public interface AlgorithmSettings extends Cloneable {

	// Getters

	public Integer getInteger(String key);

	public Double getDouble(String key);

	public String getString(String key);

	public List<String> getSubExecutions();

	// Old Getters

	public AlgorithmSetting<Integer> getIntegerSetting(String key, Integer defaultValue);

	public AlgorithmSetting<Double> getDoubleSetting(String key, Double defaultValue);

	public AlgorithmSetting<String> getStringSetting(String key, String defaultValue);

	public FromToPeriod getPeriod();

	// Mutate methods

	public void mutate(String name, Integer mutatedValue);

	public void mutate(String name, Double mutatedValue);

	public void mutate(String name, String mutatedValue);

	public void mutateSubExecution(int index, String value);

	// hash code and clone

	public void stringHashCode(StringBuilder sb);

	public AlgorithmSettings clone();

}