package stsc.algorithms.primitive;

import java.util.Date;

import stsc.algorithms.StockAlgorithm;
import stsc.algorithms.StockSignal;
import stsc.common.Day;

public class TestingStockAlgorithm extends StockAlgorithm {

	@Override
	public Class<? extends StockSignal> registerSignalsClass() {
		return null;
	}

	@Override
	public void process(Date date, String stockName, Day day) {
	}

}
