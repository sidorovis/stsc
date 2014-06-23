package stsc.general.simulator.multistarter.genetic;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.AlgorithmSettings;
import stsc.general.simulator.multistarter.MpIterator;
import stsc.general.simulator.multistarter.ParameterList;
import stsc.general.simulator.multistarter.ParameterType;

public class AlgorithmSettingsGeneticList {

	private final FromToPeriod period;
	private final ParameterList[] parameters;

	public AlgorithmSettingsGeneticList(final FromToPeriod period, ParameterList[] parameters) {
		this.period = period;
		this.parameters = new ParameterList[parameters.length];
		for (int i = 0; i < parameters.length; ++i) {
			this.parameters[i] = parameters[i].clone();
		}
	}

	@Override
	public String toString() {
		String result = period.toString();
		parameters.toString();
		for (ParameterList p : parameters) {
			result += "\n" + p.type.toString() + " " + p.toString();
		}
		return result;
	}

	public AlgorithmSettings generateRandom() {
		final AlgorithmSettingsImpl algoSettings = new AlgorithmSettingsImpl(period);

		for (int i = 0; i < ParameterType.typesSize; ++i) {
			final ParameterList list = parameters[i];
			for (MpIterator<?> p : list.getParams()) {
				final String name = p.getName();
				final String value = p.getRangom().toString();
				algoSettings.set(name, value);
			}
		}
		final ParameterList list = parameters[ParameterType.subExecutionType.getValue()];
		for (MpIterator<?> p : list.getParams()) {
			final String subExecutionName = p.getRangom().toString();
			algoSettings.addSubExecutionName(subExecutionName);
		}
		return algoSettings;
	}

}
