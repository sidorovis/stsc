package stsc.algorithms.stock.factors.primitive;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.LocalDate;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.In;
import stsc.algorithms.StockAlgorithm;
import stsc.algorithms.stock.factors.primitive.Diff;
import stsc.algorithms.stock.factors.primitive.Ema;
import stsc.algorithms.stock.factors.primitive.Sma;
import stsc.common.Day;
import stsc.common.Stock;
import stsc.common.UnitedFormatStock;
import stsc.signals.BadSignalException;
import stsc.signals.DoubleSignal;
import stsc.testhelper.TestAlgorithmsHelper;
import junit.framework.TestCase;

public class DiffTest extends TestCase {
	public void testDiff() throws BadAlgorithmException, IOException, BadSignalException {

		final StockAlgorithm.Init stockInit = TestAlgorithmsHelper.getStockAlgorithmInit("in", "aapl");
		stockInit.settings.set("e", "open");
		final In in = new In(stockInit);

		StockAlgorithm.Init emaInit = TestAlgorithmsHelper.getStockAlgorithmInit("ema", "aapl", stockInit.signalsStorage);
		emaInit.settings.addSubExecutionName("in");
		Ema ema = new Ema(emaInit);

		StockAlgorithm.Init smaInit = TestAlgorithmsHelper.getStockAlgorithmInit("sma", "aapl", stockInit.signalsStorage);
		smaInit.settings.addSubExecutionName("in");
		Sma sma = new Sma(smaInit);

		StockAlgorithm.Init diffInit = TestAlgorithmsHelper.getStockAlgorithmInit("diff", "aapl", stockInit.signalsStorage);
		diffInit.settings.addSubExecutionName("ema").addSubExecutionName("sma");
		Diff diff = new Diff(diffInit);

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			ema.process(day);
			sma.process(day);
			diff.process(day);
		}

		assertNotNull(stockInit.signalsStorage.getStockSignal("aapl", "diff", 0));
		assertEquals(0.0, stockInit.signalsStorage.getStockSignal("aapl", "diff", 0).getSignal(DoubleSignal.class).value, 0.001);
		assertEquals(0.928176, stockInit.signalsStorage.getStockSignal("aapl", "diff", 4).getSignal(DoubleSignal.class).value, 0.001);
	}
}
