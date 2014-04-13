package stsc.simulator.multistarter;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodExecution;
import stsc.common.FromToPeriod;

public class MultiEodExecution extends MultiExecution<EodExecution> {

	public MultiEodExecution(String executionName, String algorithmName, FromToPeriod period)
			throws BadAlgorithmException {
		super(executionName, algorithmName, period);

		// generate algorithm just for testing
		EodExecution.generateAlgorithm(algorithmName);
	}

	@Override
	public EodExecution next() {
		final EodExecution result = getCurrentExecution();
		generateNext();
		return result;
	}

	@Override
	protected EodExecution getCurrentExecution() {
		try {
			return new EodExecution(executionName, algorithmName, generageSettings());
		} catch (BadAlgorithmException e) {
			return null;
		}
	}

}
