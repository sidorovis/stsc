package stsc.statistic;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import stsc.common.MarketDataContext;
import stsc.common.StockInterface;
import stsc.storage.StockStorage;
import stsc.storage.StockStorageCache;
import stsc.trading.Side;
import stsc.trading.TradingLog;
import junit.framework.TestCase;

public class EquityCurveTest extends TestCase {
	static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private void loadStockFromFileSystem(String stockName, StockStorage stockStorage) {
		MarketDataContext marketDataContext = new MarketDataContext();
		marketDataContext.filteredDataFolder = "./test_data";
		StockInterface stock = marketDataContext.getStockFromFileSystem(stockName);
		if (stock != null)
			stockStorage.updateStock(stock);
	}

	public void testEquityCurve() throws ClassNotFoundException, IOException, InterruptedException, ParseException {

		StockStorage stockStorage = new StockStorageCache();
		loadStockFromFileSystem("aapl", stockStorage);
		loadStockFromFileSystem("ibm", stockStorage);
		
		TradingLog tradingLog = new TradingLog();
		tradingLog.addBuyRecord(dateFormat.parse("2013-09-03"), "aapl", Side.SHORT, 100);
		tradingLog.addBuyRecord(dateFormat.parse("2013-09-04"), "aapl", Side.SHORT, 200);
		tradingLog.addBuyRecord(dateFormat.parse("2013-09-05"), "aapl", Side.SHORT, 350);
		
		tradingLog.addSellRecord(dateFormat.parse("2013-09-06"), "aapl", Side.SHORT, 650);

		EquityCurve equityCurve = new EquityCurve(tradingLog, stockStorage);
		equityCurve.hashCode(); 
	}
}
