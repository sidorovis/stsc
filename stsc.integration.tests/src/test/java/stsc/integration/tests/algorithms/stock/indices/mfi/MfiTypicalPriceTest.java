package stsc.integration.tests.algorithms.stock.indices.mfi;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.mfi.MfiTypicalPrice;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class MfiTypicalPriceTest {

	@Test
	public void testMfiTypicalPrice() throws ParseException, IOException, BadAlgorithmException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper mfiTpInit = new StockAlgoInitHelper("mfiTp", "aapl", stockInit.getStorage());
		mfiTpInit.getSettings().setInteger("size", 10000);
		final MfiTypicalPrice mfiTp = new MfiTypicalPrice(mfiTpInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			mfiTp.process(day);

			final double typicalPrice = (day.getPrices().getHigh() + day.getPrices().getLow() + day.getPrices().getClose()) / 3;
			Assert.assertEquals(typicalPrice,
					mfiTpInit.getStorage().getStockSignal("aapl", "mfiTp", day.getDate()).getSignal(DoubleSignal.class).getValue(),
					Settings.doubleEpsilon);
		}
	}
}
