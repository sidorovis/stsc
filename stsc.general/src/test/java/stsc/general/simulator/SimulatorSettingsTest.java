package stsc.general.simulator;

import java.util.HashSet;
import java.util.Set;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.trading.TradeProcessorInit;
import junit.framework.TestCase;

public class SimulatorSettingsTest extends TestCase {
	public void testSimulatorSettings() throws BadAlgorithmException {
		final TradeProcessorInit init = new TradeProcessorInit("./test_data/simulator_tests/ndays.ini");
		final SimulatorSettings ss = new SimulatorSettings(0, init);

		final TradeProcessorInit initToEqual = new TradeProcessorInit("./test_data/simulator_tests/ndays.ini");
		final SimulatorSettings ssToEqual = new SimulatorSettings(0, initToEqual);

		assertEquals(ss.stringHashCode().hashCode(), ssToEqual.stringHashCode().hashCode());
		assertTrue(ss.stringHashCode().equals(ssToEqual.stringHashCode()));

		final Set<String> set = new HashSet<>();
		set.add(ss.stringHashCode());
		set.add(ssToEqual.stringHashCode());
		assertEquals(1, set.size());
	}
}
