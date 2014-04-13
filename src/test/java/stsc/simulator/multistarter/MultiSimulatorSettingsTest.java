package stsc.simulator.multistarter;

import junit.framework.TestCase;
import stsc.algorithms.BadAlgorithmException;
import stsc.common.FromToPeriod;
import stsc.storage.AlgorithmsStorage;
import stsc.testhelper.TestHelper;

public class MultiSimulatorSettingsTest extends TestCase {

	private String getFullAlgorithmName(String subName) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getStock(subName).getName();
	}

	public void testMultiSimulatorSettings() throws BadAlgorithmException, BadParameterException {
		MultiSimulatorSettings settings = new MultiSimulatorSettings();

		final FromToPeriod period = TestHelper.getPeriod();
		final MultiStockExecution in = new MultiStockExecution("in", getFullAlgorithmName("In"), period);
		in.addParameter(new MpString("e", "open"));
		settings.add(in);

		final MultiStockExecution sma = new MultiStockExecution("sma", getFullAlgorithmName("Sma"), period);
		sma.addParameter(new MpInteger("n", 1, 25, 1));
		settings.add(sma);
	}
}
