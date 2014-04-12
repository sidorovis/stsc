package stsc.simulator.multistarter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	private boolean finished;

	private class ParameterList {
		private final ArrayList<MpIterator<?>> params = new ArrayList<MpIterator<?>>();
		private int index;

		public ParameterList() {
			index = 0;
		}

		public void add(final MpIterator<?> mpi) {
			params.add(mpi);
		}

		public void reset() {
			index = 0;
		}

		public void increment() {
			index += 1;
		}

		public boolean empty() {
			return params.isEmpty();
		}

		public boolean hasNext() {
			return index + 1 < params.size();
		}

		public MpIterator<?> getCurrentParam() {
			return params.get(index);
		}

		public List<MpIterator<?>> getParams() {
			return params;
		}

		@Override
		public String toString() {
			return String.valueOf(index) + ": " + params.toString();
		}

	}

	private enum ParameterType {
		integerType(0), doubleType(1), stringType(2), subExecutionType(3), size(4);
		public static int typesSize = 3;

		private int value;

		ParameterType(final int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

	}

	private final ParameterList[] parameters = { new ParameterList(), new ParameterList(), new ParameterList(),
			new ParameterList() };

	public MultiStockExecution(String executionName, String algorithmName, FromToPeriod period)
			throws BadAlgorithmException {
		this.executionName = executionName;
		this.algorithmName = algorithmName;
		this.period = period;
		this.finished = false;

		// generate algorithm just for testing
		StockExecution.generateAlgorithm(algorithmName);
	}

	public void addIntegerParameter(final MpInteger parameter) {
		parameters[ParameterType.integerType.getValue()].add(parameter);
	}

	public void addDoubleParameter(final MpDouble parameter) {
		parameters[ParameterType.doubleType.getValue()].add(parameter);
	}

	public void addStringParameter(final MpString parameter) {
		parameters[ParameterType.stringType.getValue()].add(parameter);
	}

	public void addSubExecutionParameter(final MpSubExecution parameter) {
		parameters[ParameterType.subExecutionType.getValue()].add(parameter);
	}

	@Override
	public boolean hasNext() {
		if (finished)
			return false;
		for (int i = 0; i < ParameterType.size.getValue(); ++i) {
			ParameterList list = parameters[i];
			if (list.index < list.params.size())
				return true;
		}
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

	private void generateNext() {
		int parameterIndex = 0;
		while (parameterIndex < ParameterType.size.getValue()) {
			final ParameterList list = parameters[parameterIndex];
			if (list.empty()) {
				parameterIndex += 1;
				continue;
			}
			final MpIterator<?> iterator = list.getCurrentParam();
			if (iterator.hasNext()) {
				iterator.increment();
				list.reset();
				return;
			} else {
				iterator.reset();
				if (list.hasNext()) {
					list.increment();
				} else {
					list.reset();
					parameterIndex += 1;
				}
			}
		}
		finished = true;
		return;
	}
}
