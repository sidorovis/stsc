package stsc.integration.tests.algorithms;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.ListOfDoubleAdapter;
import stsc.algorithms.stock.indices.bb.BollingerBands;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class ListOfDoubleAdapterTest {

	@Test
	public void testListOfDoubleAdapter() throws ParseException, BadAlgorithmException, IOException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");
		stockInit.getSettings().setString("e", "open");
		final Input in = new Input(stockInit.getInit());

		final StockAlgoInitHelper bbInit = new StockAlgoInitHelper("bb", "aapl", stockInit.getStorage());
		bbInit.getSettings().addSubExecutionName("in");
		bbInit.getSettings().setInteger("size", 10000);
		bbInit.getSettings().setDouble("K", 1.4);
		final BollingerBands bb = new BollingerBands(bbInit.getInit());

		final StockAlgoInitHelper adapterInit = new StockAlgoInitHelper("adapter", "aapl", stockInit.getStorage());
		adapterInit.getSettings().addSubExecutionName("bb");
		adapterInit.getSettings().setInteger("size", 10000);
		final ListOfDoubleAdapter adapter = new ListOfDoubleAdapter(adapterInit.getInit());

		final StockAlgoInitHelper adapterHighInit = new StockAlgoInitHelper("adapterHigh", "aapl", stockInit.getStorage());
		adapterHighInit.getSettings().addSubExecutionName("bb");
		adapterHighInit.getSettings().setInteger("size", 10000);
		adapterHighInit.getSettings().setInteger("I", 1);
		final ListOfDoubleAdapter adapterHigh = new ListOfDoubleAdapter(adapterHighInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			bb.process(day);
			adapter.process(day);
			adapterHigh.process(day);
		}

		final Day lastDay = days.get(days.size() - 1);

		final Double smaValue = bbInit.getStorage().getStockSignal("aapl", "BB_Sma_bb", lastDay.getDate()).getSignal(DoubleSignal.class)
				.getValue();
		final Double stDevValue = bbInit.getStorage().getStockSignal("aapl", "BB_StDev_bb", lastDay.getDate())
				.getSignal(DoubleSignal.class).getValue();

		final Double bbLowValue = adapterInit.getStorage().getStockSignal("aapl", "adapter", lastDay.getDate())
				.getSignal(DoubleSignal.class).getValue();

		final Double bbHighValue = adapterHighInit.getStorage().getStockSignal("aapl", "adapterHigh", lastDay.getDate())
				.getSignal(DoubleSignal.class).getValue();

		Assert.assertEquals(smaValue - 1.4 * stDevValue, bbLowValue, Settings.doubleEpsilon);
		Assert.assertEquals(smaValue + 1.4 * stDevValue, bbHighValue, Settings.doubleEpsilon);
	}

}
