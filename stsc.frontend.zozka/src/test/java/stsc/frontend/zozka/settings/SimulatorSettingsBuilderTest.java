package stsc.frontend.zozka.settings;

import junit.framework.TestCase;

public class SimulatorSettingsBuilderTest extends TestCase {
	public void testSettingsInputDialog() throws InterruptedException {
		SimulatorSettingsBuilder.run("./test_data", new String[] { "--dataSubFolder=./", "--filteredDataSubFolder=./" });

		// SimulatorSettingsGridFactory f = new
		// SimulatorSettingsGridFactory(stockStorage, period);
	}
}
