package stsc.integration.tests.algorithms.stock.indices.msi;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.indices.msi.McClellanOscillator;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class McClellanOscillatorTest {

	@Test
	public void testMcClellanOscillator() throws ParseException, BadAlgorithmException, IOException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");
		stockInit.getSettings().setString("e", "open");
		final Input inAlgo = new Input(stockInit.getInit());

		final StockAlgoInitHelper msiInit = new StockAlgoInitHelper("msi", "aapl", stockInit.getStorage());
		msiInit.getSettings().addSubExecutionName("in");
		final McClellanOscillator msi = new McClellanOscillator(msiInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			inAlgo.process(day);
			msi.process(day);

			final double slow = stockInit.getStorage().getStockSignal("aapl", "msi_SlowEma", day.getDate()).getSignal(DoubleSignal.class)
					.getValue();
			final double fast = stockInit.getStorage().getStockSignal("aapl", "msi_FastEma", day.getDate()).getSignal(DoubleSignal.class)
					.getValue();

			Assert.assertEquals(slow - fast,
					stockInit.getStorage().getStockSignal("aapl", "msi", day.getDate()).getSignal(DoubleSignal.class).getValue(),
					Settings.doubleEpsilon);
		}
	}
}
