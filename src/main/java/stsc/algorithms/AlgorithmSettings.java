package stsc.algorithms;

import java.util.ArrayList;
import java.util.HashMap;

public class AlgorithmSettings {

	private final HashMap<String, Object> settings = new HashMap<>();
	private final ArrayList<String> subExecutions = new ArrayList<>();

	public void addSubExecutionName(final String subExecutionName) {
		subExecutions.add(subExecutionName);
	}

	public AlgorithmSettings set(final String key, final String value) {
		settings.put(key, value);
		return this;
	}

	public <T> AlgorithmSettings set(final String key, final T value) {
		settings.put(key, value);
		return this;
	}

	public <T> AlgorithmSettings get(final String key, AlgorithmSetting<T> setting) {
		final Object o = settings.get(key);
		if (o != null)
			setting.setValue(o);
		return this;
	}

	public String toString(final String key) {
		return (String) settings.get(key);
	}

	public String get(final String key) {
		final Object o = settings.get(key);
		if (o != null)
			return settings.get(key).toString();
		else
			return null;
	}
}
