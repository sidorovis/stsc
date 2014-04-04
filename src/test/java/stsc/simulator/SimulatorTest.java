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
		new Simulator("./test_data/simulator_tests/one_side.ini");
//		assertEquals(12429, new File("./test/statistics.csv").length());
	}
	
//	public void testSimpleSimulator() throws Exception {
//		deleteFileIfExists("./test/statistics.csv");
//		new Simulator("./test_data/simulator_tests/simple.ini");
//		assertEquals(12429, new File("./test/statistics.csv").length());
//	}
//
//	public void testPositiveNDaysSimulator() throws Exception {
//		deleteFileIfExists("./test/statistics.csv");
//		new Simulator("./test_data/simulator_tests/ndays.ini");
//		assertEquals(0, new File("./test/statistics.csv").length());
//	}

}
