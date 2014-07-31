package stsc.general.simulator.multistarter.grid;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.AlgorithmSettings;
import stsc.general.simulator.multistarter.AlgorithmParameters;
import stsc.general.simulator.multistarter.MpIterator;
import stsc.general.simulator.multistarter.ParameterList;
import stsc.general.simulator.multistarter.ParameterType;
import stsc.general.simulator.multistarter.ResetableIterable;
import stsc.general.simulator.multistarter.ResetableIterator;

public class AlgorithmSettingsGridIterator implements ResetableIterable<AlgorithmSettings> {

	public class Element implements ResetableIterator<AlgorithmSettings>, Cloneable {

		private final AlgorithmParameters parameters;
		private boolean finished;

		public Element(AlgorithmParameters parameterList, boolean finished) {
			this.parameters = parameterList;
			this.finished = finished;
		}

		public Element clone() {
			final AlgorithmParameters copyParameters = new AlgorithmParameters(parameters);
			return new Element(copyParameters, this.finished);
		}

		@Override
		public boolean hasNext() {
			if (finished)
				return false;
			if (parameters.getIntegers().hasCurrent())
				return true;
			if (parameters.getDoubles().hasCurrent())
				return true;
			if (parameters.getStrings().hasCurrent())
				return true;
			if (parameters.getSubExecutions().hasCurrent())
				return true;
			return false;
		}

		@Override
		public AlgorithmSettingsImpl next() {
			final AlgorithmSettingsImpl result = generateSettings();
			generateNext();
			return result;
		}

		@Override
		public void remove() {
		}

		public AlgorithmSettings current() {
			return generateSettings();
		}

		protected void generateNext() {
			ParameterType currentType = ParameterType.integerType;
			while (currentType != ParameterType.size) {
				final ParameterList<?> list = parameters.getParameters()[currentType.getValue()];
				if (list.empty()) {
					currentType = ParameterType.values()[currentType.getValue() + 1];
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
					currentType = ParameterType.values()[currentType.getValue() + 1];
				}
			}
			finished = true;
			return;
		}

		protected AlgorithmSettingsImpl generateSettings() {
			final AlgorithmSettingsImpl algoSettings = new AlgorithmSettingsImpl(period);
			for (int i = 0; i < ParameterType.typesSize; ++i) {
				final ParameterList<?> list = parameters.getParameters()[i];
				for (MpIterator<?> p : list.getParams()) {
					final String name = p.currentParameter().getName();
					final String value = p.currentParameter().getStringValue();
					algoSettings.set(name, value);
				}
			}
			final ParameterList<String> list = parameters.getSubExecutions();
			for (MpIterator<String> p : list.getParams()) {
				final String subExecutionName = p.currentParameter().getValue();
				algoSettings.addSubExecutionName(subExecutionName);
			}
			return algoSettings;
		}

		public void reset() {
			finished = false;
			parameters.reset();
		}

		public long size() {
			return parameters.size();
		}
	}

	private final FromToPeriod period;
	private final AlgorithmParameters parameters;
	private boolean finished;

	public AlgorithmSettingsGridIterator(final FromToPeriod period, final boolean finished, final AlgorithmParameters parameters) {
		this.period = period;
		this.parameters = new AlgorithmParameters(parameters);
		this.finished = finished;
	}

	@Override
	public Element iterator() {
		return new Element(this.parameters, this.finished);
	}

	public Element getResetIterator() {
		return new Element(this.parameters, this.finished);
	}

	public boolean getFinished() {
		return this.finished;
	}

	@Override
	public String toString() {
		return period.toString() + parameters.toString();
	}

	public long size() {
		return parameters.size();
	}
}
