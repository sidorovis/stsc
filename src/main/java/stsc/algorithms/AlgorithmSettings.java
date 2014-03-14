package stsc.algorithms;

import java.util.HashMap;

public class AlgorithmSettings {
	private HashMap<String, Object> settings = new HashMap<>();

	public AlgorithmSettings set(final String key, final String value) {
		settings.put(key, value);
		return this;
	}

	public <T> AlgorithmSettings set(final String key, final T value) {
		settings.put(key, value);
		return this;
	}

	public String get(final String key) {
		return (String) settings.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> AlgorithmSettings get(final String key, T type) {
		final Object o = settings.get(key);
		type = (T) o;
		return this;
	}
}
