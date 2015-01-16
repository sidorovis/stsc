package stsc.integration.tests.eod.indices.adl;

import java.util.HashMap;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.eod.indices.adl.AdlAdl;
import stsc.algorithms.eod.indices.adl.Adln;
import stsc.algorithms.eod.indices.adl.Adlt;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.storage.StockStorage;
import stsc.general.trading.BrokerImpl;
import stsc.integration.tests.helper.EodAlgoInitHelper;
import stsc.signals.DoubleSignal;
import stsc.storage.SignalsStorageImpl;
import stsc.storage.mocks.StockStorageMock;

public class AdlTest {

	private Stock getStock(String stockName) {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();
		return stockStorage.getStock(stockName).get();
	}

	private double p(String stockName, int index) {
		final Day d = getStock(stockName).getDays().get(index);
		return d.getPrices().getClose();
	}

	@Test
	public void testAdlAdl() throws Exception {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();

		final int aaplIndex = getStock("aapl").findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final int admIndex = getStock("adm").findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final int spyIndex = getStock("spy").findDayIndex(new LocalDate(2011, 9, 4).toDate());

		final BrokerImpl broker = new BrokerImpl(stockStorage);
		final SignalsStorageImpl signalsStorage = new SignalsStorageImpl();

		final EodAlgoInitHelper adlInit = new EodAlgoInitHelper("adl", signalsStorage, broker);
		final AdlAdl adl = new AdlAdl(adlInit.getInit());
		final EodAlgoInitHelper adlnInit = new EodAlgoInitHelper("adln", signalsStorage, broker);
		final Adln adln = new Adln(adlnInit.getInit());
		final EodAlgoInitHelper adltInit = new EodAlgoInitHelper("adlt", signalsStorage, broker);
		final Adlt adlt = new Adlt(adltInit.getInit());

		double previosAdl = 0;
		double previosAdln = 0;
		double previosAdlt = 0;

		for (int i = 0; i < 400; ++i) {
			final HashMap<String, Day> datafeed = new HashMap<>();
			final Day d1 = getStock("aapl").getDays().get(aaplIndex + i);
			final Day d2 = getStock("adm").getDays().get(admIndex + i);
			final Day d3 = getStock("spy").getDays().get(spyIndex + i);

			datafeed.put("aapl", d1);
			datafeed.put("adm", d2);
			datafeed.put("spy", d3);

			adl.process(d1.getDate(), datafeed);
			adln.process(d1.getDate(), datafeed);
			adlt.process(d1.getDate(), datafeed);

			if (i > 0) {
				double aaplP = p("aapl", aaplIndex + i) - p("aapl", aaplIndex + i - 1);
				double admP = p("adm", admIndex + i) - p("adm", admIndex + i - 1);
				double spyP = p("spy", spyIndex + i) - p("spy", spyIndex + i - 1);

				double at = 0.0;
				double dt = 0.0;
				double ut = 0.0;

				if (aaplP > 0.0) {
					at += 1.0;
				} else if (aaplP < 0.0) {
					dt += 1.0;
				} else {
					ut += 1.0;
				}
				if (admP > 0.0) {
					at += 1.0;
				} else if (admP < 0.0) {
					dt += 1.0;
				} else {
					ut += 1.0;
				}
				if (spyP > 0.0) {
					at += 1.0;
				} else if (spyP < 0.0) {
					dt += 1.0;
				} else {
					ut += 1.0;
				}
				previosAdl += at - dt + ut;
				previosAdln += (at + ut - dt) / (at + ut + dt);
				previosAdlt += (at - dt) / (at + ut + dt);

				final double signalValue = signalsStorage.getEodSignal("adl", d1.getDate()).getSignal(DoubleSignal.class).getValue();
				final double signalValueN = signalsStorage.getEodSignal("adln", d1.getDate()).getSignal(DoubleSignal.class).getValue();
				final double signalValueT = signalsStorage.getEodSignal("adlt", d1.getDate()).getSignal(DoubleSignal.class).getValue();

				Assert.assertEquals(previosAdl, signalValue, Settings.doubleEpsilon);
				Assert.assertEquals(previosAdln, signalValueN, Settings.doubleEpsilon);
				Assert.assertEquals(previosAdlt, signalValueT, Settings.doubleEpsilon);
			}
		}
	}
}
