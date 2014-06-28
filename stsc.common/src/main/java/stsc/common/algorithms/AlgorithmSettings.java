package stsc.common.algorithms;

import java.util.List;

import stsc.common.FromToPeriod;

public interface AlgorithmSettings extends Cloneable {

	public List<String> getSubExecutions();

	public String get(String key);

	public Integer getInteger(String key) throws BadAlgorithmException;

	public void getInteger(String key, AlgorithmSetting<Integer> setting);

	public AlgorithmSetting<Integer> getIntegerSetting(String key, Integer defaultValue);

	public AlgorithmSetting<Double> getDoubleSetting(String key, Double defaultValue);

	public AlgorithmSetting<String> getStringSetting(String key, String defaultValue);

	public Double getDouble(String key) throws BadAlgorithmException;

	public void getDouble(String key, AlgorithmSetting<Double> setting);

	public <T> void get(String key, AlgorithmSetting<T> setting) throws BadAlgorithmException;

	public FromToPeriod getPeriod();

	public void stringHashCode(StringBuilder sb);

	public void mutate(String name, String stringValue);

	public AlgorithmSettings clone();

}