package stsc.general.simulator.multistarter.genetic;

import java.util.Random;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.AlgorithmSettings;
import stsc.common.algorithms.StockExecution;
import stsc.general.simulator.multistarter.MpIterator;
import stsc.general.simulator.multistarter.Parameter;
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

	public void mutate(AlgorithmSettings settings) {
		int parametersAmount = 0;
		for (ParameterList list : parameters) {
			parametersAmount += list.getParams().size();
		}
		if (parametersAmount > 0) {
			mutateParameter(settings, parametersAmount);
		}
	}

	private void mutateParameter(AlgorithmSettings settings, int parametersAmount) {
		final Random random = new Random();
		int indexOfMutatingParameter = random.nextInt(parametersAmount);
		for (ParameterList list : parameters) {
			final int size = list.getParams().size();
			if (size != 0 && size > indexOfMutatingParameter) {
				final MpIterator<?> parameter = list.getParams().get(indexOfMutatingParameter);
				final int sizeOfValues = (int) parameter.size();
				final int mutatedIndex = random.nextInt(sizeOfValues);
				final Parameter<?> p = parameter.getParameterByIndex(mutatedIndex);
				settings.mutate(p.getName(), p.getStringValue());
				break;
			} else {
				indexOfMutatingParameter -= list.getParams().size();
			}
		}
	}

	public AlgorithmSettings mergeStock(AlgorithmSettings leftSe, AlgorithmSettings rightSe) {
		final AlgorithmSettingsImpl result = new AlgorithmSettingsImpl(period);

		for (ParameterList list : parameters) {
			for(MpIterator<?> p : list.getParams()) {
				p.getName();
// TODO create merge
			}
		}

		return result;
	}
}
