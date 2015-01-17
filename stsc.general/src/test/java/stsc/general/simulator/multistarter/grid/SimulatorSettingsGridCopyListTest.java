package stsc.general.simulator.multistarter.grid;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.testhelper.TestGridSimulatorSettings;
import stsc.storage.mocks.StockStorageMock;

public class SimulatorSettingsGridCopyListTest {

	@Test
	public void testSimulatorSettingsGridCopyList() throws BadAlgorithmException, BadParameterException {
		final StockStorage stockStorage = new StockStorageMock();

		final SimulatorSettingsGridFactory factory = TestGridSimulatorSettings.getSmallGridFactory(stockStorage,
				Arrays.asList(new String[] { "open", "close", "high", "low" }), "31-01-2000");
		final SimulatorSettingsGridList listExternal = factory.getCopyList();

		int count = 0;
		for (SimulatorSettings ssExternal : listExternal) {
			Assert.assertNotNull(ssExternal);
			final SimulatorSettingsGridList listInternal = factory.getCopyList();
			for (SimulatorSettings ssInternal : listInternal) {
				count += 1;
				Assert.assertNotNull(ssInternal);
			}
		}
		Assert.assertEquals(factory.size() * factory.size(), count);
	}
}
