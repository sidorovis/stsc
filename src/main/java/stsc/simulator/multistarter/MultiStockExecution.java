package stsc.simulator.multistarter;

import java.util.ArrayList;
import java.util.Iterator;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.StockExecution;
import stsc.common.FromToPeriod;

public class MultiStockExecution implements Iterator<StockExecution> {

	private class StockExecutionIterator implements Iterable<StockExecution> {
		private final MultiStockExecution multiStockExecution;

		public StockExecutionIterator(MultiStockExecution multiStockExecution) {
			this.multiStockExecution = multiStockExecution;
		}

		@Override
		public Iterator<StockExecution> iterator() {
			return multiStockExecution;
		}
	}

	private final String executionName;
	private final String algorithmName;
	private final FromToPeriod period;

	private final ArrayList<MpInteger> integerParameters = new ArrayList<>();
	private final ArrayList<MpDouble> doubleParameters = new ArrayList<>();
	private final ArrayList<MpString> stringParameters = new ArrayList<>();
	private final ArrayList<MpSubExecution> subExecutionParameters = new ArrayList<>();

	private int integerIndex;
	private int doubleIndex;
	private int stringIndex;
	private int subExecutionIndex;

	public MultiStockExecution(String executionName, String algorithmName, FromToPeriod period) {
		this.executionName = executionName;
		this.algorithmName = algorithmName;
		this.period = period;

		integerIndex = 0;
		doubleIndex = 0;
		stringIndex = 0;
		subExecutionIndex = 0;
	}

	public void addIntegerParameter(final MpInteger parameter) {
		integerParameters.add(parameter);
	}

	public void addDoubleParameter(final MpDouble parameter) {
		doubleParameters.add(parameter);
	}

	public void addStringParameter(final MpString parameter) {
		stringParameters.add(parameter);
	}

	public void addSubExecutionParameter(final MpSubExecution parameter) {
		subExecutionParameters.add(parameter);
	}

	@Override
	public boolean hasNext() {
		if (integerIndex < integerParameters.size())
			return true;
		if (doubleIndex < doubleParameters.size())
			return true;
		if (stringIndex < stringParameters.size())
			return true;
		if (subExecutionIndex < subExecutionParameters.size())
			return true;
		return false;
	}

	@Override
	public StockExecution next() {
		StockExecution result = getCurrentExecution();
		generateNext();
		return result;
	}

	public StockExecutionIterator getEntry() {
		return new StockExecutionIterator(this);
	}

	@Override
	public void remove() {
	}

	private StockExecution getCurrentExecution() {
		try {
			return new StockExecution(executionName, algorithmName, generageSettings());
		} catch (BadAlgorithmException e) {
			return null;
		}
	}

	private AlgorithmSettings generageSettings() {
		final AlgorithmSettings algoSettings = new AlgorithmSettings(period);

		for (MpInteger p : integerParameters) {
			final String name = p.current().getName();
			final String value = p.current().getStringName();
			algoSettings.set(name, value);
		}
		for (MpDouble p : doubleParameters) {
			final String name = p.current().getName();
			final String value = p.current().getStringName();
			algoSettings.set(name, value);
		}
		for (MpString p : stringParameters) {
			final String name = p.current().getName();
			final String value = p.current().getStringName();
			algoSettings.set(name, "'" + value + "'");
		}
		for (MpSubExecution p : subExecutionParameters) {
			final String subExecutionName = p.current().getStringName();
			algoSettings.addSubExecutionName(subExecutionName);
		}
		return algoSettings;
	}

	private void generateNext() {
		boolean stepFinishElement = false;
//		stepFinishElement = makeStep(integerIndex, integerParameters);
//		if (stepFinishElement)
//			return;
//		stepFinishElement = makeStep(doubleIndex, doubleParameters);
//		if (stepFinishElement)
//			return;
//		stepFinishElement = makeStep(stringIndex, stringParameters);
//		if (stepFinishElement)
//			return;
//		stepFinishElement = makeStep(subExecutionIndex, subExecutionParameters);
//		if (stepFinishElement)
//			return;
	}
//
//	private boolean makeStep(int index, ArrayList<? extends MpIterator<?>> list) {
//		if (index < list.size()) {
//			final MpIterator<?> subList = list.get(index);
//			if (!makeStep(subList))
//				index += 1;
//			if (index == list.size())
//				return false;
//		}
//		return false;
//	}
//
//	private boolean makeStep(MpIterator<?> parameter) {
//		if (parameter.hasNext()) {
//			parameter.increment();
//			return true;
//		} else {
//			parameter.reset();
//			return false;
//		}
//	}
}
