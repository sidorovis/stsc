package stsc.simulator;

import stsc.common.FromToPeriod;
import stsc.storage.StockStorage;

public class SimulatorSettings {
	StockStorage stockStorage;
	FromToPeriod period;

//	ExecutionsStorage executionsStorage;
//	String outputStatisticsFile;
	
	public SimulatorSettings(StockStorage stockStorage, FromToPeriod period) {
		this.stockStorage = stockStorage;
		this.period = period;
	}
}
