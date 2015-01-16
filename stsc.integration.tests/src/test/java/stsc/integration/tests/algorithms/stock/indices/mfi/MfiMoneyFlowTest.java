package stsc.integration.tests.algorithms.stock.indices.mfi;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.mfi.MfiMoneyFlow;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class MfiMoneyFlowTest {

	@Test
	public void testMfiMoneyFlow() throws ParseException, BadAlgorithmException, IOException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper mfiMfInit = new StockAlgoInitHelper("mfiMf", "aapl", stockInit.getStorage());
		mfiMfInit.getSettings().setInteger("size", 10000);
		final MfiMoneyFlow mfiMf = new MfiMoneyFlow(mfiMfInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			mfiMf.process(day);

			final double tp = stockInit.getStorage().getStockSignal("aapl", "mfiMf_mfiTp", day.getDate()).getContent(DoubleSignal.class)
					.getValue();

			Assert.assertEquals(tp * day.getVolume(),
					mfiMfInit.getStorage().getStockSignal("aapl", "mfiMf", day.getDate()).getContent(DoubleSignal.class).getValue(),
					Settings.doubleEpsilon);
		}
	}

}
