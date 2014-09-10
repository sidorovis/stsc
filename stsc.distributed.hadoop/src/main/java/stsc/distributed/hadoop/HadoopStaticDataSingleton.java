package stsc.distributed.hadoop;

import java.io.IOException;
import java.text.ParseException;

import stsc.common.FromToPeriod;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridFactory;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.storage.ThreadSafeStockStorage;
import stsc.yahoo.YahooFileStockStorage;

class HadoopStaticDataSingleton {

	// StockStorage

	private static StockStorage stockStorage = null;

	static StockStorage getInstance(final String dataFolder, final String filteredDataFolder) throws ClassNotFoundException, IOException, InterruptedException {
		if (stockStorage == null) {
			stockStorage = new YahooFileStockStorage(dataFolder, filteredDataFolder);
		}
		return stockStorage;
	}

	static StockStorage getInstance() {
		if (stockStorage == null) {
			stockStorage = new ThreadSafeStockStorage();
			try {
				stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf"));
				stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/adm.uf"));
				stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/spy.uf"));
			} catch (IOException e) {
			}
		}
		return stockStorage;
	}

	public static SimulatorSettingsGridList getGridList() {
		try {
			FromToPeriod period = new FromToPeriod("01-01-2000", "01-01-2014");
			final SimulatorSettingsGridFactory factory = new SimulatorSettingsGridFactory(getInstance(), period);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	//

}