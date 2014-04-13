package stsc.simulator.multistarter;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.StockExecution;
import stsc.common.FromToPeriod;

public class MultiStockExecution extends MultiExecution<StockExecution> {

	public MultiStockExecution(String executionName, String algorithmName, FromToPeriod period)
			throws BadAlgorithmException {
		super(executionName, algorithmName, period);
		// generate algorithm just for testing
		StockExecution.generateAlgorithm(algorithmName);
	}

	@Override
	public StockExecution next() {
		final StockExecution result = getCurrentExecution();
		generateNext();
		return result;
	}

	@Override
	protected StockExecution getCurrentExecution() {
		try {
			return new StockExecution(executionName, algorithmName, generageSettings());
		} catch (BadAlgorithmException e) {
			return null;
		}
	}

}
