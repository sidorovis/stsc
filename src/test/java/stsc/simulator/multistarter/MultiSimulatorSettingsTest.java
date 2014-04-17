package stsc.simulator.multistarter;

import java.util.Arrays;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.StockAlgorithm;
import stsc.common.FromToPeriod;
import stsc.simulator.MultiSimulatorSettings;
import stsc.simulator.SimulatorSettings;
import stsc.storage.AlgorithmsStorage;
import stsc.storage.ExecutionsStorage;
import stsc.storage.StockStorage;
import stsc.testhelper.StockStorageHelper;
import stsc.testhelper.TestHelper;
import stsc.trading.Broker;
import junit.framework.TestCase;

public class MultiSimulatorSettingsTest { // extends TestCase {

	// private String algoName(String aname) throws BadAlgorithmException {
	// return AlgorithmsStorage.getInstance().getStock(aname).getName();
	// }
	//
	// // public void testEmptyMultiSimulatorSettings() throws
	// // BadAlgorithmException, BadParameterException {
	// // final StockStorage stockStorage = new StockStorageHelper();
	// // final FromToPeriod period = TestHelper.getPeriod();
	// //
	// // final MultiSimulatorSettings settings = new
	// // MultiSimulatorSettings(stockStorage, period);
	// // int count = 0;
	// // for (SimulatorSettings simulatorSettings : settings) {
	// // count += 1;
	// // assertNotNull(simulatorSettings);
	// // }
	// // assertEquals(0, count);
	// // }
	//
	// public void testMultiSimulatorSettings() throws BadAlgorithmException,
	// BadParameterException {
	// final StockStorage stockStorage = new StockStorageHelper();
	// final FromToPeriod period = TestHelper.getPeriod();
	//
	// final MultiSimulatorSettings settings = new
	// MultiSimulatorSettings(stockStorage, period);
	// MultiAlgorithmSettings in = new MultiAlgorithmSettings(period);
	// in.add(new MpString("e", Arrays.asList(new String[] { "1", "2" })));
	// MultiAlgorithmSettings otherIn = new MultiAlgorithmSettings(period);
	// otherIn.add(new MpString("e", Arrays.asList(new String[] { "3", "4" })));
	// settings.addStock("e", algoName("In"), in);
	// settings.addStock("e2", algoName("In"), otherIn);
	//
	// // MultiAlgorithmSettings ema = new MultiAlgorithmSettings(period);
	// // ema.add(new MpDouble("P", 0.1, 0.2, 0.1));
	// // ema.add(new MpSubExecution("e", Arrays.asList(new String[] { "e" })));
	// // settings.addStock("ema", algoName("Ema"), ema);
	//
	// int count = 0;
	// for (SimulatorSettings simulatorSettings : settings) {
	// count += 1;
	// final ExecutionsStorage executionsStorage =
	// simulatorSettings.getInit().getExecutionsStorage();
	// executionsStorage.initialize(new Broker(stockStorage));
	// final StockAlgorithm sain = executionsStorage.getStockAlgorithm("e",
	// "aapl");
	// final StockAlgorithm saine2 = executionsStorage.getStockAlgorithm("e2",
	// "aapl");
	// // final StockAlgorithm emain =
	// executionsStorage.getStockAlgorithm("ema", "aapl");
	// assertNotNull(sain);
	// // assertNotNull(emain);
	// }
	// assertEquals(45, count);
	// }
}
