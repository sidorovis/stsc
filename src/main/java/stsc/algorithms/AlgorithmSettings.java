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

public class AlgorithmSettings implements Cloneable {

	private final FromToPeriod period;
	private final HashMap<String, String> settings;
	private final ArrayList<String> subExecutions;

	public AlgorithmSettings(final FromToPeriod period) {
		this.period = period;
		this.settings = new HashMap<>();
		this.subExecutions = new ArrayList<>();
	}

	public static AlgorithmSettings read(ObjectInput in) throws IOException {
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
		return new AlgorithmSettings(period, settings, subExecutions);
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

	@Override
	public int hashCode() {
		int result = 1;
		for (Map.Entry<String, String> i : settings.entrySet()) {
			result *= (i.getKey().hashCode() + i.getValue().hashCode());
		}
		for (int i = 0; i < subExecutions.size(); ++i) {
			result *= (i + subExecutions.get(i).hashCode());
		}
		return result;
	}
}
