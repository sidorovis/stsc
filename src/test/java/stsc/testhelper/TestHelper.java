package stsc.testhelper;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.StockAlgorithm;
import stsc.storage.SignalsStorage;
import stsc.storage.ThreadSafeStockStorage;
import stsc.trading.Broker;

public class TestHelper {
	public static EodAlgorithm.Init getEodAlgorithmInit() {
		EodAlgorithm.Init init = new EodAlgorithm.Init();
		init.broker = new Broker(new ThreadSafeStockStorage());
		init.executionName = "eName";
		init.settings = new AlgorithmSettings();
		init.signalsStorage = new SignalsStorage();
		return init;
	}

	public static StockAlgorithm.Init getStockAlgorithmInit() {
		StockAlgorithm.Init init = new StockAlgorithm.Init();
		init.executionName = "eName";
		init.settings = new AlgorithmSettings();
		init.signalsStorage = new SignalsStorage();
		init.stockName = "sName";
		return init;
	}
}
