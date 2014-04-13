package stsc.simulator.multistarter;

import java.util.Iterator;

import stsc.algorithms.AlgorithmSettings;
import stsc.common.FromToPeriod;

public class MultiAlgorithmSettings implements Iterator<AlgorithmSettings> {

	private final FromToPeriod period;
	private boolean finished;

	private final ParameterList[] parameters = { new ParameterList(), new ParameterList(), new ParameterList(),
			new ParameterList() };

	public MultiAlgorithmSettings(final FromToPeriod period) {
		this.period = period;
		this.finished = false;
	}

	@Override
	public boolean hasNext() {
		if (finished)
			return false;
		for (int i = 0; i < ParameterType.size.getValue(); ++i) {
			ParameterList list = parameters[i];
			if (list.hasNext())
				return true;
		}
		return false;
	}

	@Override
	public AlgorithmSettings next() {
		final AlgorithmSettings result = generageSettings();
		generateNext();
		return result;
	}

	@Override
	public void remove() {
	}

	public void add(final MpInteger parameter) {
		parameters[ParameterType.integerType.getValue()].add(parameter);
	}

	public void add(final MpDouble parameter) {
		parameters[ParameterType.doubleType.getValue()].add(parameter);
	}

	public void add(final MpString parameter) {
		parameters[ParameterType.stringType.getValue()].add(parameter);
	}

	public void add(final MpSubExecution parameter) {
		parameters[ParameterType.subExecutionType.getValue()].add(parameter);
	}

	protected AlgorithmSettings generageSettings() {
		final AlgorithmSettings algoSettings = new AlgorithmSettings(period);

		for (int i = 0; i < ParameterType.typesSize; ++i) {
			final ParameterList list = parameters[i];
			for (MpIterator<?> p : list.getParams()) {
				final String name = p.current().getName();
				final String value = p.current().getStringName();
				algoSettings.set(name, value);
			}
		}
		final ParameterList list = parameters[ParameterType.subExecutionType.getValue()];
		for (MpIterator<?> p : list.getParams()) {
			final String subExecutionName = p.current().getStringName();
			algoSettings.addSubExecutionName(subExecutionName);
		}
		return algoSettings;
	}

	protected void generateNext() {
		int parameterIndex = 0;
		while (parameterIndex < ParameterType.size.getValue()) {
			final ParameterList list = parameters[parameterIndex];
			if (list.empty()) {
				parameterIndex += 1;
				continue;
			}
			final MpIterator<?> iterator = list.getCurrentParam();
			iterator.increment();
			if (iterator.hasNext()) {
				list.reset();
				return;
			}
			iterator.reset();
			if (list.hasNext()) {
				list.increment();
			} else {
				list.reset();
				parameterIndex += 1;
			}
		}
		finished = true;
		return;
	}

	public void reset() {
		finished = false;
		for (ParameterList list : parameters) {
			list.reset();
		}
	}

	public class AlgorithmSettingsIterator extends ExecutionIterator<AlgorithmSettings> {
		public AlgorithmSettingsIterator(Iterator<AlgorithmSettings> execution) {
			super(execution);
		}
	}

	public AlgorithmSettingsIterator getEntry() {
		return new AlgorithmSettingsIterator(this);
	}

}
