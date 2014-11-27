package stsc.general.simulator;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.trading.TradeProcessorInit;

public class SimulatorSettingsTest {

	@Test
	public void testSimulatorSettings() throws BadAlgorithmException {
		final TradeProcessorInit init = new TradeProcessorInit(new File("./test_data/simulator_tests/ndays.ini"));
		final SimulatorSettings ss = new SimulatorSettings(0, init);

		final TradeProcessorInit initToEqual = new TradeProcessorInit(new File("./test_data/simulator_tests/ndays.ini"));
		final SimulatorSettings ssToEqual = new SimulatorSettings(0, initToEqual);

		Assert.assertEquals(ss.stringHashCode().hashCode(), ssToEqual.stringHashCode().hashCode());
		Assert.assertTrue(ss.stringHashCode().equals(ssToEqual.stringHashCode()));

		final Set<String> set = new HashSet<>();
		set.add(ss.stringHashCode());
		set.add(ssToEqual.stringHashCode());
		Assert.assertEquals(1, set.size());
	}

	@Test
	public void testSimulatorSettingsToString() throws BadAlgorithmException {
		final TradeProcessorInit init = new TradeProcessorInit(new File("./test_data/simulator_tests/ndays.ini"));
		final SimulatorSettings ss = new SimulatorSettings(0, init);
		Assert.assertEquals(10, ss.toString().split("\n").length);
	}
}
