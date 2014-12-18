package stsc.integration.tests.eod.indices.adl;

import java.util.HashMap;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.eod.indices.adl.AdlAdl;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.storage.StockStorage;
import stsc.general.trading.BrokerImpl;
import stsc.integration.tests.helper.EodAlgoInitHelper;
import stsc.signals.eod.EodDoubleSignal;
import stsc.storage.SignalsStorageImpl;
import stsc.storage.mocks.StockStorageMock;

public class AdlAdlTest {

	private double p(String stockName, int index) {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();
		final Day d = stockStorage.getStock(stockName).getDays().get(index);
		return d.getPrices().getClose();
	}

	private int compare(double priceDifference) {
		if (priceDifference >= 0.0) {
			return 1;
		} else {
			return -1;
		}
	}

	@Test
	public void testAdlAdl() throws Exception {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();

		final int aaplIndex = stockStorage.getStock("aapl").findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final int admIndex = stockStorage.getStock("adm").findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final int spyIndex = stockStorage.getStock("spy").findDayIndex(new LocalDate(2011, 9, 4).toDate());

		final BrokerImpl broker = new BrokerImpl(stockStorage);
		final SignalsStorageImpl signalsStorage = new SignalsStorageImpl();

		final EodAlgoInitHelper adlInit = new EodAlgoInitHelper("adl", signalsStorage, broker);
		final AdlAdl adl = new AdlAdl(adlInit.getInit());

		int previosAdl = 0;

		for (int i = 0; i < 400; ++i) {
			final HashMap<String, Day> dataset = new HashMap<>();
			final Day d1 = stockStorage.getStock("aapl").getDays().get(aaplIndex + i);
			final Day d2 = stockStorage.getStock("adm").getDays().get(admIndex + i);
			final Day d3 = stockStorage.getStock("spy").getDays().get(spyIndex + i);

			dataset.put("aapl", d1);
			dataset.put("adm", d2);
			dataset.put("spy", d3);

			adl.process(d1.getDate(), dataset);

			if (i > 0) {
				double aaplP = p("aapl", aaplIndex + i) - p("aapl", aaplIndex + i - 1);
				double admP = p("adm", admIndex + i) - p("adm", admIndex + i - 1);
				double spyP = p("spy", spyIndex + i) - p("spy", spyIndex + i - 1);
				previosAdl += compare(aaplP) + compare(admP) + compare(spyP);

				final double signalValue = signalsStorage.getEodSignal("adl", d1.getDate()).getSignal(EodDoubleSignal.class).getValue();

				Assert.assertEquals(Double.valueOf(previosAdl), signalValue, Settings.doubleEpsilon);
			}
		}
	}
}
