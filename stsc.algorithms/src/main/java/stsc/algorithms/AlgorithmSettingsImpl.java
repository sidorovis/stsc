package stsc.algorithms;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.AlgorithmSetting;
import stsc.common.algorithms.AlgorithmSettings;
import stsc.common.algorithms.BadAlgorithmException;

public final class AlgorithmSettingsImpl implements Cloneable, AlgorithmSettings {

	private final FromToPeriod period;
	private final HashMap<String, Integer> integers;
	private final HashMap<String, Double> doubles;
	private final HashMap<String, String> settings;
	private final ArrayList<String> subExecutions;

	public AlgorithmSettingsImpl(final FromToPeriod period) {
		this.period = period;
		this.integers = new HashMap<>();
		this.doubles = new HashMap<>();
		this.settings = new HashMap<>();
		this.subExecutions = new ArrayList<>();
	}

	public static AlgorithmSettings read(final ObjectInput in) throws IOException {
		final FromToPeriod period = FromToPeriod.read(in);
		final int settingsSize = in.readInt();
		final HashMap<String, String> settings = new HashMap<>();
		for (int i = 0; i < settingsSize; ++i) {
			final String key = in.readUTF();
			final String value = in.readUTF();
			settings.put(key, value);
		}
		final int subExecutionsSize = in.readInt();
		final ArrayList<String> subExecutions = new ArrayList<>();
		for (int i = 0; i < subExecutionsSize; ++i) {
			final String name = in.readUTF();
			subExecutions.add(name);
		}
		return new AlgorithmSettingsImpl(period, settings, subExecutions);
	}

	private AlgorithmSettingsImpl(FromToPeriod p, HashMap<String, String> settings, ArrayList<String> executions) {
		this.period = p;
		this.integers = new HashMap<String, Integer>();
		this.doubles = new HashMap<String, Double>();
		this.settings = new HashMap<String, String>(settings);
		this.subExecutions = new ArrayList<String>(executions);
	}

	private AlgorithmSettingsImpl(final AlgorithmSettingsImpl cloneFrom) {
		this.period = cloneFrom.period;
		this.integers = new HashMap<String, Integer>(cloneFrom.integers);
		this.doubles = new HashMap<String, Double>(cloneFrom.doubles);
		this.settings = new HashMap<String, String>(cloneFrom.settings);
		this.subExecutions = new ArrayList<String>(cloneFrom.subExecutions);
	}

	public AlgorithmSettingsImpl addSubExecutionName(final String subExecutionName) {
		subExecutions.add(subExecutionName);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see stsc.algorithms.AlgorithmSettings#getSubExecutions()
	 */
	@Override
	public List<String> getSubExecutions() {
		return subExecutions;
	}

	public AlgorithmSettings set(final String key, final String value) {
		settings.put(key, value);
		return this;
	}

	public AlgorithmSettings setInteger(final String key, final Integer value) {
		integers.put(key, value);
		return this;
	}

	public AlgorithmSettings setDouble(final String key, final Double value) {
		doubles.put(key, value);
		return this;
	}

	public <T> AlgorithmSettings set(final String key, final T value) {
		settings.put(key, value.toString());
		return this;
	}

	public String toString(final String key) {
		return (String) settings.get(key);
	}

	@Override
	public String toString() {
		return settings.toString() + " " + subExecutions.toString();
	}

	@Override
	public String get(final String key) {
		return settings.get(key);
	}

	@Override
	public Integer getInteger(final String key) throws BadAlgorithmException {
		return integers.get(key);
	}

	@Override
	public void getInteger(final String key, final AlgorithmSetting<Integer> setting) {
		final Integer value = integers.get(key);
		if (value != null) {
			setting.setInteger(integers.get(key));
		}
	}

	@Override
	public AlgorithmSetting<Integer> getIntegerSetting(final String key, final Integer defaultValue) {
		final Integer value = integers.get(key);
		if (value == null) {
			return new AlgorithmSettingImpl<Integer>(defaultValue);
		}
		return new AlgorithmSettingImpl<Integer>(value);
	}

	@Override
	public AlgorithmSetting<Double> getDoubleSetting(final String key, final Double defaultValue) {
		final Double value = doubles.get(key);
		if (value == null) {
			return new AlgorithmSettingImpl<Double>(defaultValue);
		}
		return new AlgorithmSettingImpl<Double>(value);
	}

	@Override
	public AlgorithmSetting<String> getStringSetting(final String key, final String defaultValue) {
		final String value = settings.get(key);
		if (value == null) {
			return new AlgorithmSettingImpl<String>(defaultValue);
		}
		return new AlgorithmSettingImpl<String>(value);
	}

	@Override
	public Double getDouble(final String key) throws BadAlgorithmException {
		return doubles.get(key);
	}

	@Override
	public void getDouble(final String key, final AlgorithmSetting<Double> setting) {
		final Double value = doubles.get(key);
		if (value != null) {
			setting.setDouble(doubles.get(key));
		}
	}

	@Override
	public <T> void get(final String key, final AlgorithmSetting<T> setting) throws BadAlgorithmException {
		final String value = settings.get(key);
		if (value != null) {
			final Class<T> clazz = setting.getClassType();
			if (clazz.equals(Integer.class)) {
				setting.setInteger(Integer.valueOf(value));
				return;
			}
			if (clazz.equals(Double.class)) {
				setting.setDouble(Double.valueOf(value));
				return;
			}
			if (clazz.equals(String.class)) {
				setting.setString(String.valueOf(value));
				return;
			}
			try {
				final Class<?>[] params = { String.class };
				final Constructor<T> constructor = clazz.getConstructor(params);
				final Object[] args = { value };
				final T v = constructor.newInstance(args);
				setting.setValue(v);
			} catch (Exception e) {
				throw new BadAlgorithmException("Problem with parsing parameter " + key + " " + value + " can't be parsed to " + clazz.getName()
						+ ". Due error: " + e.getMessage());
			}
		}
	}

	@Override
	public AlgorithmSettingsImpl clone() {
		return new AlgorithmSettingsImpl(this);
	}

	@Override
	public FromToPeriod getPeriod() {
		return period;
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		period.writeExternal(out);
		out.writeInt(settings.size());
		for (Map.Entry<String, String> i : settings.entrySet()) {
			out.writeUTF(i.getKey());
			out.writeUTF(i.getValue());
		}
		out.writeInt(subExecutions.size());
		for (String i : subExecutions) {
			out.writeUTF(i);
		}
	}

	public void stringHashCode(StringBuilder sb) {
		for (Map.Entry<String, String> i : settings.entrySet()) {
			sb.append(i.getKey()).append(i.getValue());
		}
		for (String string : subExecutions) {
			sb.append(string);
		}
	}

}
