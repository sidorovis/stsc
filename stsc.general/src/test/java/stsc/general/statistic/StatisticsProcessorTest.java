package stsc.general.statistic;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.Side;
import stsc.common.stocks.Stock;
import stsc.common.storage.StockStorage;
import stsc.general.trading.BrokerImpl;
import stsc.general.trading.TradingLog;
import stsc.storage.mocks.StockStorageMock;

public class StatisticsProcessorTest {

	private Day gd(Stock s, int i) {
		return s.getDays().get(i);
	}

	private Date gdd(Stock s, int i) {
		return gd(s, i).getDate();
	}

	@Test
	public void testStatistics() throws Exception {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();
		final Stock aapl = stockStorage.getStock("aapl");
		final Stock adm = stockStorage.getStock("adm");

		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());

		final TradingLog tradingLog = new BrokerImpl(stockStorage).getTradingLog();

		final StatisticsProcessor statistics = new StatisticsProcessor(tradingLog);

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));

		final int aaplLongSize = 100;
		final int admShortSize = 200;

		tradingLog.addBuyRecord(gdd(aapl, aaplIndex), "aapl", Side.LONG, aaplLongSize);
		tradingLog.addBuyRecord(gdd(adm, admIndex), "adm", Side.SHORT, admShortSize);

		statistics.processEod();

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));

		final double aaplLongIn = gd(aapl, aaplIndex).getPrices().getOpen();
		final double admShortIn = gd(adm, admIndex).getPrices().getOpen();

		tradingLog.addSellRecord(gdd(aapl, aaplIndex), "aapl", Side.LONG, aaplLongSize);
		tradingLog.addSellRecord(gdd(adm, admIndex), "adm", Side.SHORT, admShortSize);

		statistics.processEod();

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));

		final double aaplLongOut = gd(aapl, aaplIndex).getPrices().getOpen();
		final double admShortOut = gd(adm, admIndex).getPrices().getOpen();

		statistics.processEod();

		final Statistics statisticsData = statistics.calculate();

		final double aaplPriceDiff = aaplLongSize * aaplLongOut * (1.0 - statistics.getCommision()) - aaplLongIn * aaplLongSize
				* (1.0 + statistics.getCommision());
		final double admPriceDiff = admShortSize * admShortOut * (1.0 - statistics.getCommision()) - admShortSize * admShortIn
				* (1.0 + statistics.getCommision());

		Assert.assertEquals(3.0, statisticsData.getPeriod(), Settings.doubleEpsilon);
		Assert.assertEquals(aaplPriceDiff, statisticsData.getMaxWin(), Settings.doubleEpsilon);
		Assert.assertEquals(admPriceDiff, statisticsData.getMaxLoss(), Settings.doubleEpsilon);
		Assert.assertEquals(aaplPriceDiff - admPriceDiff, statisticsData.getEquityCurveInMoney().getLastElement().value,
				Settings.doubleEpsilon);
	}

	@Test
	public void testReverseStatistics() throws Exception {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();
		final Stock aapl = stockStorage.getStock("aapl");
		final Stock adm = stockStorage.getStock("adm");

		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());

		TradingLog tradingLog = new BrokerImpl(stockStorage).getTradingLog();

		StatisticsProcessor statistics = new StatisticsProcessor(tradingLog);

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));

		final int aaplLongSize = 100;
		final int admShortSize = 200;

		tradingLog.addBuyRecord(gdd(aapl, aaplIndex), "aapl", Side.SHORT, aaplLongSize);
		tradingLog.addBuyRecord(gdd(adm, admIndex), "adm", Side.LONG, admShortSize);

		statistics.processEod();

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));

		final double aaplLongIn = gd(aapl, aaplIndex).getPrices().getOpen();
		final double admShortIn = gd(adm, admIndex).getPrices().getOpen();

		tradingLog.addSellRecord(gdd(aapl, aaplIndex), "aapl", Side.SHORT, aaplLongSize);
		tradingLog.addSellRecord(gdd(adm, admIndex), "adm", Side.LONG, admShortSize);

		statistics.processEod();

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));

		final double aaplLongOut = gd(aapl, aaplIndex).getPrices().getOpen();
		final double admShortOut = gd(adm, admIndex).getPrices().getOpen();

		statistics.processEod();

		Statistics statisticsData = statistics.calculate();

		final double aaplPriceDiff = aaplLongSize * aaplLongOut * (1.0 - statistics.getCommision()) - aaplLongIn * aaplLongSize
				* (1.0 + statistics.getCommision());
		final double admPriceDiff = admShortSize * admShortOut * (1.0 - statistics.getCommision()) - admShortSize * admShortIn
				* (1.0 + statistics.getCommision());

		Assert.assertEquals(3.0, statisticsData.getPeriod(), Settings.doubleEpsilon);
		Assert.assertEquals(aaplPriceDiff, statisticsData.getMaxLoss(), Settings.doubleEpsilon);
		Assert.assertEquals(admPriceDiff, statisticsData.getMaxWin(), Settings.doubleEpsilon);
		Assert.assertEquals(admPriceDiff - aaplPriceDiff, statisticsData.getEquityCurveInMoney().getLastElement().value,
				Settings.doubleEpsilon);
	}

	@Test
	public void testProbabilityStatistics() throws IOException {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();
		final Stock aapl = stockStorage.getStock("aapl");
		final Stock adm = stockStorage.getStock("adm");
		final Stock spy = stockStorage.getStock("spy");

		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int spyIndex = spy.findDayIndex(new LocalDate(2013, 9, 4).toDate());

		final TradingLog tradingLog = new BrokerImpl(stockStorage).getTradingLog();

		final StatisticsProcessor statistics = new StatisticsProcessor(tradingLog);

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));
		statistics.setStockDay("spy", gd(spy, ++spyIndex));

		tradingLog.addBuyRecord(gdd(aapl, aaplIndex), "aapl", Side.SHORT, 100);
		tradingLog.addBuyRecord(gdd(adm, admIndex), "adm", Side.LONG, 200);
		tradingLog.addBuyRecord(gdd(spy, spyIndex), "spy", Side.LONG, 30);

		statistics.processEod();

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));

		final double aaplLongIn = gd(aapl, aaplIndex).getPrices().getOpen();
		final double admShortIn = gd(adm, admIndex).getPrices().getOpen();
		final double spyLongIn = gd(spy, spyIndex).getPrices().getOpen();

		spyIndex++;

		tradingLog.addBuyRecord(gdd(aapl, aaplIndex), "aapl", Side.SHORT, 100);
		tradingLog.addBuyRecord(gdd(adm, admIndex), "adm", Side.LONG, 500);

		statistics.processEod();

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));
		statistics.setStockDay("spy", gd(spy, ++spyIndex));

		statistics.processEod();

		tradingLog.addSellRecord(gdd(aapl, aaplIndex), "aapl", Side.SHORT, 200);
		tradingLog.addSellRecord(gdd(adm, admIndex), "adm", Side.LONG, 700);
		tradingLog.addSellRecord(gdd(spy, spyIndex), "spy", Side.LONG, 30);

		statistics.processEod();
		
		final Statistics statisticsData = statistics.calculate();

		Assert.assertEquals(4.0, statisticsData.getPeriod(), Settings.doubleEpsilon);
		Assert.assertEquals(-1.045815, statisticsData.getAvGain(), Settings.doubleEpsilon);

		Assert.assertEquals(0.75, statisticsData.getFreq(), Settings.doubleEpsilon);
		Assert.assertEquals(0.333333, statisticsData.getWinProb(), Settings.doubleEpsilon);

		Assert.assertEquals(18.027313, statisticsData.getAvWin(), Settings.doubleEpsilon);
		Assert.assertEquals(681.888817, statisticsData.getAvLoss(), Settings.doubleEpsilon);

		Assert.assertEquals(18.027313, statisticsData.getMaxWin(), Settings.doubleEpsilon);
		Assert.assertEquals(1117.831259, statisticsData.getMaxLoss(), Settings.doubleEpsilon);

		Assert.assertEquals(0.0264373, statisticsData.getAvWinAvLoss(), Settings.doubleEpsilon);
		Assert.assertEquals(-24.883543, statisticsData.getKelly(), Settings.doubleEpsilon);
	}

	@Test
	public void testEquityCurveOn518DaysStatistics() throws IOException {
		final Statistics stats = testTradingHelper(518, true);

		Assert.assertEquals(-24.841541, stats.getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(0.301158, stats.getFreq(), Settings.doubleEpsilon);

		Assert.assertEquals(413.572015, stats.getAvWin(), Settings.doubleEpsilon);
		Assert.assertEquals(-0.128536, stats.getKelly(), Settings.doubleEpsilon);

		Assert.assertEquals(-0.912834, stats.getSharpeRatio(), Settings.doubleEpsilon);

		Assert.assertEquals(-0.993661, stats.getStartMonthAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(5.382562, stats.getStartMonthStdDevGain(), Settings.doubleEpsilon);
		Assert.assertEquals(16.374432, stats.getStartMonthMax(), Settings.doubleEpsilon);
		Assert.assertEquals(-8.657443, stats.getStartMonthMin(), Settings.doubleEpsilon);

		Assert.assertEquals(-26.557413, stats.getMonth12AvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(10.281048, stats.getMonth12StdDevGain(), Settings.doubleEpsilon);
		Assert.assertEquals(0.0, stats.getMonth12Max(), Settings.doubleEpsilon);
		Assert.assertEquals(-40.114969, stats.getMonth12Min(), Settings.doubleEpsilon);

		Assert.assertEquals(104.142857, stats.getDdDurationAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(675.0, stats.getDdDurationMax(), Settings.doubleEpsilon);
		Assert.assertEquals(10.509182, stats.getDdValueAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(58.198999, stats.getDdValueMax(), Settings.doubleEpsilon);
	}

	@Test
	public void testEquityCurveOn251DaysStatistics() throws IOException {
		Statistics stats = testTradingHelper(251, true);

		Assert.assertEquals(-11.791114, stats.getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(0.298804, stats.getFreq(), Settings.doubleEpsilon);

		Assert.assertEquals(493.100176, stats.getAvWin(), Settings.doubleEpsilon);
		Assert.assertEquals(-0.098393, stats.getKelly(), Settings.doubleEpsilon);

		Assert.assertEquals(-0.716106, stats.getSharpeRatio(), Settings.doubleEpsilon);

		Assert.assertEquals(-0.982592, stats.getStartMonthAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(6.569471, stats.getStartMonthStdDevGain(), Settings.doubleEpsilon);
		Assert.assertEquals(17.712646, stats.getStartMonthMax(), Settings.doubleEpsilon);
		Assert.assertEquals(-7.076923, stats.getStartMonthMin(), Settings.doubleEpsilon);

		Assert.assertEquals(-11.791114, stats.getMonth12AvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(0.0, stats.getMonth12StdDevGain(), Settings.doubleEpsilon);
		Assert.assertEquals(0.0, stats.getMonth12Max(), Settings.doubleEpsilon);
		Assert.assertEquals(-11.791114, stats.getMonth12Min(), Settings.doubleEpsilon);

		Assert.assertEquals(48.571428, stats.getDdDurationAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(286.0, stats.getDdDurationMax(), Settings.doubleEpsilon);
		Assert.assertEquals(7.838447, stats.getDdValueAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(38.248115, stats.getDdValueMax(), Settings.doubleEpsilon);
	}

	@Test
	public void testStatisticsOnLastClose() throws IOException, IllegalArgumentException, IllegalAccessException {
		final Statistics stats = testTradingHelper(3, false);
		stats.print("./test/out.csv");

		Assert.assertEquals(0.0, stats.getDdValueMax(), Settings.doubleEpsilon);
		final File file = new File("./test/out.csv");
		Assert.assertTrue(file.exists());
		Assert.assertEquals(458.0, file.length(), Settings.doubleEpsilon);
		file.delete();
	}

	private Statistics testTradingHelper(int daysCount, boolean closeOnExit) throws IOException {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();
		final Stock aapl = stockStorage.getStock("aapl");
		final Stock adm = stockStorage.getStock("adm");
		final Stock spy = stockStorage.getStock("spy");

		int aaplIndex = aapl.findDayIndex(new LocalDate(2008, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2008, 9, 4).toDate());
		int spyIndex = spy.findDayIndex(new LocalDate(2008, 9, 4).toDate());

		TradingLog tradingLog = new BrokerImpl(stockStorage).getTradingLog();

		StatisticsProcessor statisticsProcessor = new StatisticsProcessor(tradingLog);

		final int buySellEach = 5;
		boolean opened = false;

		for (int i = 0; i < daysCount; ++i) {

			statisticsProcessor.setStockDay("aapl", gd(aapl, ++aaplIndex));
			statisticsProcessor.setStockDay("adm", gd(adm, ++admIndex));
			statisticsProcessor.setStockDay("spy", gd(spy, ++spyIndex));

			if (i % buySellEach == 0 && i % (buySellEach * 2) == 0) {
				tradingLog.addBuyRecord(gdd(aapl, aaplIndex), "aapl", Side.SHORT, 100);
				tradingLog.addBuyRecord(gdd(adm, admIndex), "adm", Side.LONG, 200);
				tradingLog.addBuyRecord(gdd(spy, spyIndex), "spy", Side.SHORT, 100);
				opened = true;
			}
			if (i % buySellEach == 0 && i % (buySellEach * 2) != 0) {
				tradingLog.addSellRecord(gdd(aapl, aaplIndex), "aapl", Side.SHORT, 100);
				tradingLog.addSellRecord(gdd(adm, admIndex), "adm", Side.LONG, 200);
				tradingLog.addSellRecord(gdd(spy, spyIndex), "spy", Side.SHORT, 100);
				opened = false;
			}

			if ((i == (daysCount - 1)) && opened && closeOnExit) {
				tradingLog.addSellRecord(gdd(aapl, aaplIndex), "aapl", Side.SHORT, 100);
				tradingLog.addSellRecord(gdd(adm, admIndex), "adm", Side.LONG, 200);
				tradingLog.addSellRecord(gdd(spy, spyIndex), "spy", Side.SHORT, 100);
				opened = false;
			}

			statisticsProcessor.processEod();
		}

		Statistics stats = statisticsProcessor.calculate();
		Assert.assertEquals(new Double(daysCount), stats.getPeriod(), Settings.doubleEpsilon);

		return stats;
	}
}
