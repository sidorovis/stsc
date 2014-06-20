package stsc.general.simulator.multistarter.genetic;

import stsc.common.FromToPeriod;
import stsc.general.simulator.multistarter.ParameterList;

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
}
