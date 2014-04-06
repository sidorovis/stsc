package stsc.simulator;

import java.io.File;

import junit.framework.TestCase;

public class SimulatorTest extends TestCase {

	private void deleteFileIfExists(String fileName) {
		File file = new File(fileName);
		if (file.exists())
			file.delete();
	}

	public void testOneSideSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		new Simulator("./test_data/simulator_tests/one_side.ini").getStatistics().print("./test/statistics.csv");
		assertEquals(544, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

	public void testSimpleSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		new Simulator("./test_data/simulator_tests/simple.ini").getStatistics().print("./test/statistics.csv");
		assertEquals(11881, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

	public void testPositiveNDaysSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		new Simulator("./test_data/simulator_tests/ndays.ini").getStatistics().print("./test/statistics.csv");
		assertEquals(48255, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

}
