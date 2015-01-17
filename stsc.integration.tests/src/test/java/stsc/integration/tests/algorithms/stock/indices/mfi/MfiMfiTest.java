package stsc.integration.tests.algorithms.stock.indices.mfi;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.mfi.MfiMfi;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class MfiMfiTest {

	@Test
	public void testMfiMfi() throws ParseException, BadAlgorithmException, IOException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper mfiInit = new StockAlgoInitHelper("mfi", "aapl", stockInit.getStorage());
		mfiInit.getSettings().setInteger("size", 5);
		mfiInit.getSettings().setInteger("N", 5);
		final MfiMfi mfi = new MfiMfi(mfiInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		double p = 0.0;
		double n = 0.0;

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			mfi.process(day);

			if (i == aaplIndex) {
				final double mfV = stockInit.getStorage().getStockSignal("aapl", "mfi_mfiMf", day.getDate()).getContent(DoubleSignal.class)
						.getValue();
				p += mfV;
			} else {
				final double mtVprevious = stockInit.getStorage().getStockSignal("aapl", "mfi_mfiMf_mfiTp", days.get(i - 1).getDate())
						.getContent(DoubleSignal.class).getValue();
				final double mtV = stockInit.getStorage().getStockSignal("aapl", "mfi_mfiMf_mfiTp", day.getDate())
						.getContent(DoubleSignal.class).getValue();
				final double mfV = stockInit.getStorage().getStockSignal("aapl", "mfi_mfiMf", day.getDate()).getContent(DoubleSignal.class)
						.getValue();
				if (mtV >= mtVprevious) {
					p += mfV;
				} else {
					n += mfV;
				}
				if (i - aaplIndex > 5) {
					final double mtOldPreviousV = stockInit.getStorage()
							.getStockSignal("aapl", "mfi_mfiMf_mfiTp", days.get(i - 6).getDate()).getContent(DoubleSignal.class).getValue();
					final double mtOldV = stockInit.getStorage().getStockSignal("aapl", "mfi_mfiMf_mfiTp", days.get(i - 5).getDate())
							.getContent(DoubleSignal.class).getValue();
					final double mfOldV = stockInit.getStorage().getStockSignal("aapl", "mfi_mfiMf", days.get(i - 5).getDate())
							.getContent(DoubleSignal.class).getValue();
					if (mtOldV >= mtOldPreviousV) {
						p -= mfOldV;
					} else {
						n -= mfOldV;
					}
				}
				final double v = stockInit.getStorage().getStockSignal("aapl", "mfi", day.getDate()).getContent(DoubleSignal.class)
						.getValue();
				if (Double.compare(0.0, n) == 0) {
					Assert.assertEquals(v, 50.0, Settings.doubleEpsilon);
				} else {
					Assert.assertEquals(v, (100 - (100 / (1 + p / n))), Settings.doubleEpsilon);
				}
			}
		}
	}
}
