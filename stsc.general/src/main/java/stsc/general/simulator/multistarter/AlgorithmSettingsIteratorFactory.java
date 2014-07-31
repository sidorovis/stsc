package stsc.general.simulator.multistarter;

import stsc.common.FromToPeriod;
import stsc.general.simulator.multistarter.genetic.AlgorithmSettingsGeneticList;
import stsc.general.simulator.multistarter.grid.AlgorithmSettingsGridIterator;

public class AlgorithmSettingsIteratorFactory {

	private final FromToPeriod period;
	private final AlgorithmParameters parameters;
	private boolean finished;

	public AlgorithmSettingsIteratorFactory(final FromToPeriod period) {
		this.period = period;
		this.parameters = new AlgorithmParameters();
		this.finished = false;
	}

	public AlgorithmSettingsIteratorFactory add(final MpInteger parameter) {
		if (parameter.hasNext())
			this.finished = false;
		parameters.getIntegers().add(parameter);
		return this;
	}

	public AlgorithmSettingsIteratorFactory add(final MpDouble parameter) {
		if (parameter.hasNext())
			this.finished = false;
		parameters.getDoubles().add(parameter);
		return this;
	}

	public AlgorithmSettingsIteratorFactory add(final MpString parameter) {
		if (parameter.hasNext())
			this.finished = false;
		parameters.getStrings().add(parameter);
		return this;
	}

	public AlgorithmSettingsIteratorFactory add(final MpSubExecution parameter) {
		if (parameter.hasNext())
			this.finished = false;
		parameters.getSubExecutions().add(parameter);
		return this;
	}

	public AlgorithmSettingsGridIterator getGridIterator() {
		return new AlgorithmSettingsGridIterator(period, finished, parameters);
	}

	public AlgorithmSettingsGeneticList getGeneticList() {
		return new AlgorithmSettingsGeneticList(period, parameters);
	}

}
