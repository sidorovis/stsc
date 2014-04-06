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
		init.settings = AlgorithmSettings.create00s();
		init.signalsStorage = new SignalsStorage();
		return init;
	}

	public static StockAlgorithm.Init getStockAlgorithmInit(String executionName, String stockName,
			SignalsStorage storage) {
		StockAlgorithm.Init init = new StockAlgorithm.Init();
		init.executionName = executionName;
		init.settings = AlgorithmSettings.create00s();
		init.signalsStorage = storage;
		init.stockName = stockName;
		return init;
	}

	public static StockAlgorithm.Init getStockAlgorithmInit(String executionName, String stockName) {
		return getStockAlgorithmInit(executionName, stockName, new SignalsStorage());
	}

	public static StockAlgorithm.Init getStockAlgorithmInit(String executionName) {
		return getStockAlgorithmInit(executionName, "sName");
	}

	public static StockAlgorithm.Init getStockAlgorithmInit() {
		return getStockAlgorithmInit("eName");
	}
}
