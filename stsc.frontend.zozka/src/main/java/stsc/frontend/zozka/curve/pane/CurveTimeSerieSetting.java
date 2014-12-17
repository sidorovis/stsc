package stsc.frontend.zozka.curve.pane;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import stsc.common.FromToPeriod;
import stsc.common.signals.EodSignal;
import stsc.common.signals.Signal;
import stsc.common.signals.StockSignal;
import stsc.common.stocks.Stock;
import stsc.common.storage.SignalsStorage;
import stsc.frontend.zozka.gui.models.SerieXYToolTipGenerator;
import stsc.signals.DoubleSignal;
import stsc.signals.eod.EodDoubleSignal;
import stsc.storage.ExecutionsStorage;

public class CurveTimeSerieSetting extends CurveChartSetting {

	private final int index;
	private final TimeSeriesCollection timeSeriesCollection;
	private final XYItemRenderer seriesRenderer;

	public CurveTimeSerieSetting(boolean showAlgo, String title, Stock stock, int index) {
		super(showAlgo, title);
		this.index = index;
		this.timeSeriesCollection = new TimeSeriesCollection();
		final TimeSeries timeSeries = new TimeSeries(title);

		for (int i = 0; i < stock.getDays().size(); ++i) {
			stsc.common.Day day = stock.getDays().get(i);
			timeSeries.addOrUpdate(new Day(day.getDate()), day.getAdjClose());
		}

		timeSeriesCollection.addSeries(timeSeries);
		this.seriesRenderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, new SerieXYToolTipGenerator(title));
		addListenerToShowAlgorithm(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				seriesRenderer.setSeriesVisible(0, newValue);
			}
		});
	}

	public CurveTimeSerieSetting(boolean showAlgo, String title, Stock stock, int index, FromToPeriod period) {
		super(showAlgo, title);
		this.index = index;
		this.timeSeriesCollection = new TimeSeriesCollection();
		final TimeSeries timeSeries = new TimeSeries(title);

		final int from = stock.findDayIndex(period.getFrom());
		final int to = stock.findDayIndex(period.getTo()) - 1;

		for (int i = from; i < to; ++i) {
			stsc.common.Day day = stock.getDays().get(i);
			timeSeries.add(new Day(day.getDate()), day.getAdjClose());
		}

		timeSeriesCollection.addSeries(timeSeries);
		this.seriesRenderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, new SerieXYToolTipGenerator(title));
		addListenerToShowAlgorithm(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				seriesRenderer.setSeriesVisible(0, newValue);
			}
		});

	}

	public CurveTimeSerieSetting(boolean showAlgo, String title, int index, SignalsStorage signalsStorage) {
		super(showAlgo, title);
		this.index = index;
		this.timeSeriesCollection = new TimeSeriesCollection();
		final TimeSeries timeSeries = createOnEodTimeSeries(title, signalsStorage);
		timeSeriesCollection.addSeries(timeSeries);
		this.seriesRenderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, new SerieXYToolTipGenerator(title));
		addListenerToShowAlgorithm(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				seriesRenderer.setSeriesVisible(0, newValue);
			}
		});
	}

	public CurveTimeSerieSetting(boolean showAlgo, String title, String stockName, int index, SignalsStorage signalsStorage) {
		super(showAlgo, title);
		this.index = index;
		this.timeSeriesCollection = new TimeSeriesCollection();
		final TimeSeries timeSeries = createOnStockTimeSeries(title, stockName, signalsStorage);
		timeSeriesCollection.addSeries(timeSeries);
		this.seriesRenderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, new SerieXYToolTipGenerator(title));
		addListenerToShowAlgorithm(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				seriesRenderer.setSeriesVisible(0, newValue);
			}
		});
	}

	private TimeSeries createOnEodTimeSeries(String title, SignalsStorage signalsStorage) {
		final TimeSeries timeSeries = new TimeSeries(title);
		final String outName = ExecutionsStorage.outNameFor(title);
		final int size = signalsStorage.getSignalsSize(outName);
		for (int i = 0; i < size; ++i) {
			final Signal<? extends EodSignal> s = signalsStorage.getEodSignal(outName, i);
			timeSeries.add(new Day(s.getDate()), s.getSignal(EodDoubleSignal.class).getValue());
		}
		return timeSeries;
	}

	private TimeSeries createOnStockTimeSeries(String title, String stockName, SignalsStorage signalsStorage) {
		final TimeSeries timeSeries = new TimeSeries(title);
		final String outName = ExecutionsStorage.outNameFor(title);
		final int size = signalsStorage.getIndexSize(stockName, outName);
		for (int i = 0; i < size; ++i) {
			final Signal<? extends StockSignal> s = signalsStorage.getStockSignal(stockName, outName, i);
			timeSeries.add(new Day(s.getDate()), s.getSignal(DoubleSignal.class).getValue());
		}
		return timeSeries;
	}

	public TimeSeriesCollection getTimeSeriesCollection() {
		return timeSeriesCollection;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public XYItemRenderer getRenderer() {
		return seriesRenderer;
	}
}