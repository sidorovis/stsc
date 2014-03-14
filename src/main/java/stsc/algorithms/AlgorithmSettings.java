package stsc.algorithms;

import java.util.HashMap;

public class AlgorithmSettings {
	private HashMap<String, String> settings = new HashMap<>();

	public AlgorithmSettings set(final String key, final String value) {
		settings.put(key, value);
		return this;
	}

	public <T> AlgorithmSettings set(final String key, final T value) {
		settings.put(key, value.toString());
		return this;
	}

	public AlgorithmSettings set(final String key, final double value) {
		settings.put(key, new Double(value).toString());
		return this;
	}

	public String getString(final String key) {
		return settings.get(key);
	}

	public String get(final String key) {
		return settings.get(key);
	}

	public AlgorithmSettings get(final String key, final StringBuilder out) {
		final String value = settings.get(key);
		if (value != null)
			out.append(value);
		return this;
	}

}
