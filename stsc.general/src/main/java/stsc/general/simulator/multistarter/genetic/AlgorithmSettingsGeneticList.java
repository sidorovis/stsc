package stsc.general.simulator.multistarter.genetic;

import java.util.Iterator;
import java.util.Random;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.AlgorithmSettings;
import stsc.general.simulator.multistarter.AlgorithmParameters;
import stsc.general.simulator.multistarter.MpIterator;
import stsc.general.simulator.multistarter.ParameterList;

public class AlgorithmSettingsGeneticList {

	private static final int MUTATING_FINISHED = -1;

	private final FromToPeriod period;
	private final AlgorithmParameters parameters;
	final Random random = new Random();

	public AlgorithmSettingsGeneticList(final FromToPeriod period, final AlgorithmParameters parameters) {
		this.period = period;
		this.parameters = new AlgorithmParameters(parameters);
	}

	@Override
	public String toString() {
		return period.toString() + parameters.toString();
	}

	// Random generate methods

	public AlgorithmSettings generateRandom() {
		final AlgorithmSettingsImpl algoSettings = new AlgorithmSettingsImpl(period);
		generateRandomIntegers(algoSettings);
		generateRandomDoubles(algoSettings);
		generateRandomStrings(algoSettings);
		generateRandomSubExecutions(algoSettings);
		return algoSettings;
	}

	private void generateRandomIntegers(final AlgorithmSettingsImpl algoSettings) {
		final ParameterList<Integer> list = parameters.getIntegers();
		for (MpIterator<Integer> p : list.getParams()) {
			algoSettings.setInteger(p.getName(), p.getRangom());
		}
	}

	private void generateRandomDoubles(final AlgorithmSettingsImpl algoSettings) {
		final ParameterList<Double> list = parameters.getDoubles();
		for (MpIterator<Double> p : list.getParams()) {
			algoSettings.setDouble(p.getName(), p.getRangom());
		}
	}

	private void generateRandomStrings(final AlgorithmSettingsImpl algoSettings) {
		final ParameterList<String> list = parameters.getStrings();
		for (MpIterator<String> p : list.getParams()) {
			algoSettings.setString(p.getName(), p.getRangom());
		}
	}

	private void generateRandomSubExecutions(final AlgorithmSettingsImpl algoSettings) {
		final ParameterList<String> list = parameters.getSubExecutions();
		for (MpIterator<String> p : list.getParams()) {
			algoSettings.addSubExecutionName(p.getRangom());
		}
	}

	// Mutate methods

	public void mutate(final AlgorithmSettings settings) {
		final int parametersAmount = parameters.parametersSize();
		if (parametersAmount > 0) {
			int indexOfMutatingParameter = random.nextInt(parametersAmount);
			indexOfMutatingParameter = mutateIntegers(settings, indexOfMutatingParameter);
			if (elementMutated(indexOfMutatingParameter))
				return;
			indexOfMutatingParameter = mutateDoubles(settings, indexOfMutatingParameter);
			if (elementMutated(indexOfMutatingParameter))
				return;
			indexOfMutatingParameter = mutateStrings(settings, indexOfMutatingParameter);
			if (elementMutated(indexOfMutatingParameter))
				return;
			mutateSubExecutions(settings, indexOfMutatingParameter);
		}
	}

	private boolean elementMutated(final int indexOfMutatingParameter) {
		return indexOfMutatingParameter < 0;
	}

	private int mutateIntegers(final AlgorithmSettings settings, final int index) {
		if (index < 0)
			return index;
		final ParameterList<Integer> list = parameters.getIntegers();
		final int size = list.getParams().size();
		if (size != 0 && size > index) {
			final MpIterator<Integer> mutatingParameter = list.getParams().get(index);
			settings.mutate(mutatingParameter.getName(), mutate(settings, index, mutatingParameter));
			return MUTATING_FINISHED;
		}
		return index - size;
	}

	private int mutateDoubles(final AlgorithmSettings settings, final int index) {
		if (index < 0)
			return index;
		final ParameterList<Double> list = parameters.getDoubles();
		final int size = list.getParams().size();
		if (size != 0 && size > index) {
			final MpIterator<Double> mutatingParameter = list.getParams().get(index);
			settings.mutate(mutatingParameter.getName(), mutate(settings, index, mutatingParameter));
			return MUTATING_FINISHED;
		}
		return index - size;
	}

	private int mutateStrings(final AlgorithmSettings settings, final int index) {
		if (index < 0)
			return index;
		final ParameterList<String> list = parameters.getStrings();
		final int size = list.getParams().size();
		if (size != 0 && size > index) {
			final MpIterator<String> mutatingParameter = list.getParams().get(index);
			settings.mutate(mutatingParameter.getName(), mutate(settings, index, mutatingParameter));
			return MUTATING_FINISHED;
		}
		return index - size;
	}

	private void mutateSubExecutions(final AlgorithmSettings settings, final int index) {
		if (index < 0)
			return;
		final ParameterList<String> list = parameters.getSubExecutions();
		final int size = list.getParams().size();
		if (size != 0 && size > index) {
			final MpIterator<String> mutatingParameter = list.getParams().get(index);
			settings.mutateSubExecution(index, mutate(settings, index, mutatingParameter));
		}
	}

	private <T> T mutate(final AlgorithmSettings settings, int index, MpIterator<T> iterator) {
		final int sizeOfValues = (int) iterator.size();
		final int mutatedIndex = random.nextInt(sizeOfValues);
		final T mutatedValue = iterator.parameter(mutatedIndex);
		return mutatedValue;
	}

	// Merge methods

	public AlgorithmSettings merge(AlgorithmSettings leftSe, AlgorithmSettings rightSe) {
		return mergeParameters(leftSe, rightSe);
	}

	private AlgorithmSettings mergeParameters(AlgorithmSettings leftSe, AlgorithmSettings rightSe) {
		final AlgorithmSettingsImpl result = new AlgorithmSettingsImpl(period);
		mergeIntegers(result, leftSe, rightSe);
		mergeDouble(result, leftSe, rightSe);
		mergeStrings(result, leftSe, rightSe);
		mergeSubExecutions(result, leftSe, rightSe);
		return result;
	}

	private void mergeIntegers(final AlgorithmSettingsImpl result, final AlgorithmSettings leftSe, final AlgorithmSettings rightSe) {
		for (MpIterator<Integer> p : parameters.getIntegers().getParams()) {
			final String settingName = p.getName();
			final Integer resultValue = p.merge(leftSe.getInteger(settingName), rightSe.getInteger(settingName));
			result.setInteger(settingName, resultValue);
		}
	}

	private void mergeDouble(AlgorithmSettingsImpl result, AlgorithmSettings leftSe, AlgorithmSettings rightSe) {
		for (MpIterator<Double> p : parameters.getDoubles().getParams()) {
			final String settingName = p.getName();
			final Double resultValue = mutate(p, leftSe.getDouble(settingName), rightSe.getDouble(settingName));
			result.setDouble(settingName, resultValue);
		}
	}

	private void mergeStrings(AlgorithmSettingsImpl result, AlgorithmSettings leftSe, AlgorithmSettings rightSe) {
		for (MpIterator<String> p : parameters.getStrings().getParams()) {
			final String settingName = p.getName();
			final String mutatedValue = p.merge(leftSe.getString(settingName), rightSe.getString(settingName));
			result.setString(settingName, mutatedValue);
		}
	}

	private void mergeSubExecutions(AlgorithmSettingsImpl result, AlgorithmSettings leftSe, AlgorithmSettings rightSe) {
		final Iterator<MpIterator<String>> subExecutionIterator = parameters.getSubExecutionIterator();
		final Iterator<String> lv = leftSe.getSubExecutions().iterator();
		final Iterator<String> rv = rightSe.getSubExecutions().iterator();
		while (subExecutionIterator.hasNext() && lv.hasNext() && rv.hasNext()) {
			final MpIterator<String> p = subExecutionIterator.next();
			final String mutatedValue = p.merge(lv.next(), rv.next());
			result.addSubExecutionName(mutatedValue);
		}
	}

	private <Type> Type mutate(MpIterator<Type> p, Type leftValue, Type rightValue) {
		final Type mutatedValue = p.merge(leftValue, rightValue);
		return mutatedValue;
	}

	public long size() {
		return parameters.size();
	}

}
