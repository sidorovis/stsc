package stsc.general.simulator.multistarter.grid;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.AlgorithmSettings;
import stsc.general.simulator.multistarter.AlgorithmParameters;
import stsc.general.simulator.multistarter.MpIterator;
import stsc.general.simulator.multistarter.ParameterList;
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
			if (getNext(parameters.getIntegers()))
				return;
			if (getNext(parameters.getDoubles()))
				return;
			if (getNext(parameters.getStrings()))
				return;
			if (getNext(parameters.getSubExecutions()))
				return;
			finished = true;
			return;
		}

		private <T> boolean getNext(ParameterList<T> list) {
			while (true) {
				if (list.empty()) {
					return false;
				}
				final MpIterator<T> iterator = list.getCurrentParam();
				iterator.increment();
				if (iterator.hasNext()) {
					list.reset();
					return true;
				}
				iterator.reset();
				if (list.hasNext()) {
					list.increment();
				} else {
					list.reset();
					return false;
				}
			}
		}

		protected AlgorithmSettingsImpl generateSettings() {
			final AlgorithmSettingsImpl algoSettings = new AlgorithmSettingsImpl(period);

			final ParameterList<Integer> integers = parameters.getIntegers();
			for (MpIterator<Integer> p : integers.getParams()) {
				final String name = p.currentParameter().getName();
				final Integer value = p.currentParameter().getValue();
				algoSettings.setInteger(name, value);
			}

			final ParameterList<Double> doubles = parameters.getDoubles();
			for (MpIterator<Double> p : doubles.getParams()) {
				final String name = p.currentParameter().getName();
				final Double value = p.currentParameter().getValue();
				algoSettings.setDouble(name, value);
			}

			final ParameterList<String> strings = parameters.getStrings();
			for (MpIterator<String> p : strings.getParams()) {
				final String name = p.currentParameter().getName();
				final String value = p.currentParameter().getValue();
				algoSettings.setString(name, value);
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
