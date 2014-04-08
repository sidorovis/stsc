package stsc.algorithms;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import stsc.common.FromToPeriod;

public class AlgorithmSettings implements Cloneable {

	private final FromToPeriod period;
	private final HashMap<String, String> settings;
	private final ArrayList<String> subExecutions;

	public static AlgorithmSettings create00s() {
		try {
			return new AlgorithmSettings(new FromToPeriod("01-01-2000", "31-12-2009"));
		} catch (Exception e) {
		}
		return null;
	}

	public AlgorithmSettings(final FromToPeriod period) {
		this.period = period;
		this.settings = new HashMap<>();
		this.subExecutions = new ArrayList<>();
	}

	private AlgorithmSettings(final FromToPeriod period, HashMap<String, String> settings,
			ArrayList<String> subExecutions) {
		this.period = period;
		this.settings = new HashMap<String, String>(settings);
		this.subExecutions = new ArrayList<String>(subExecutions);
	}

	public AlgorithmSettings addSubExecutionName(final String subExecutionName) {
		subExecutions.add(subExecutionName);
		return this;
	}

	public List<String> getSubExecutions() {
		return subExecutions;
	}

	public AlgorithmSettings set(final String key, final String value) {
		settings.put(key, value);
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

	public String get(final String key) {
		return settings.get(key);
	}

	public <T> void get(final String key, final AlgorithmSetting<T> setting) throws BadAlgorithmException {
		final String value = settings.get(key);
		if (value != null) {
			final Class<T> clazz = setting.getClassType();
			try {
				final Class<?>[] params = { String.class };
				final Constructor<T> constructor = clazz.getConstructor(params);
				final Object[] args = { value };
				final T v = constructor.newInstance(args);
				setting.setValue(v);
			} catch (Exception e) {
				throw new BadAlgorithmException("Problem with parsing parameter " + key + " " + value
						+ " can't be parsed to " + clazz.getName() + ". Due error: " + e.getMessage());
			}
		}
	}

	@Override
	public AlgorithmSettings clone() {
		return new AlgorithmSettings(period, settings, subExecutions);
	}

	public FromToPeriod getPeriod() {
		return period;
	}

}
