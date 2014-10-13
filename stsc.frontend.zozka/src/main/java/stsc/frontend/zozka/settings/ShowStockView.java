package stsc.frontend.zozka.settings;

import java.util.Collections;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import stsc.common.FromToPeriod;
import stsc.common.signals.Signal;
import stsc.common.signals.StockSignal;
import stsc.common.stocks.Stock;
import stsc.common.storage.SignalsStorage;
import stsc.frontend.zozka.gui.models.DatasetForStock;
import stsc.signals.DoubleSignal;
import stsc.storage.ExecutionsStorage;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ShowStockView {

	private class SerieXYToolTipGenerator implements XYToolTipGenerator {

		private final String name;

		SerieXYToolTipGenerator(String name) {
			this.name = name;
		}

		@Override
		public String generateToolTip(XYDataset dataset, int series, int item) {
			return name;
		}

	}

	private final Stage stage;
	private DatasetForStock chartDataset;
	private SwingNode sn = new SwingNode();

	public ShowStockView(Stock stock, FromToPeriod period) {
		this.stage = new Stage();
		this.stage.initModality(Modality.WINDOW_MODAL);
		setCenterWidget(stock, period, Collections.emptyList(), null);
		this.stage.setMinHeight(380);
		this.stage.setMinWidth(480);
		this.stage.setHeight(380);
		this.stage.setWidth(480);
		this.stage.setTitle("Stock: " + stock.getName());
		this.stage.centerOnScreen();
		this.stage.showAndWait();
	}

	public ShowStockView(Stock stock, FromToPeriod period, List<String> executionsName, SignalsStorage signalsStorage) {
		this.stage = new Stage();
		this.stage.initModality(Modality.WINDOW_MODAL);
		setCenterWidget(stock, period, executionsName, signalsStorage);
		this.stage.setMinHeight(380);
		this.stage.setMinWidth(480);
		this.stage.setHeight(380);
		this.stage.setWidth(480);
		this.stage.setTitle("Stock: " + stock.getName());
		this.stage.centerOnScreen();
		this.stage.showAndWait();
	}

	private void setCenterWidget(final Stock stock, FromToPeriod period, List<String> executionsName, SignalsStorage signalsStorage) {
		final BorderPane root = new BorderPane();
		final Scene scene = new Scene(root);
		this.stage.setScene(scene);
		this.chartDataset = new DatasetForStock(stock, period);

		final JFreeChart chart = ChartFactory.createCandlestickChart("", "", "", chartDataset, true);
		chart.getXYPlot().setRenderer(0, new CandlestickRenderer(3));
		int index = 1;
		for (String executionName : executionsName) {
			final TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();

			final String outName = ExecutionsStorage.outNameFor(executionName);
			final int size = signalsStorage.getIndexSize(stock.getName(), outName);
			final TimeSeries ts = new TimeSeries(executionName);
			for (int i = 0; i < size; ++i) {
				final Signal<? extends StockSignal> s = signalsStorage.getStockSignal(stock.getName(), outName, i);
				ts.add(new Day(s.getDate()), s.getSignal(DoubleSignal.class).value);
			}
			timeSeriesCollection.addSeries(ts);
			chart.getXYPlot().setDataset(index, timeSeriesCollection);
			chart.getXYPlot().mapDatasetToRangeAxis(index, 0);
			final XYItemRenderer seriesRenderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, new SerieXYToolTipGenerator(
					executionName));
			chart.getXYPlot().setRenderer(index, seriesRenderer);
			chart.getXYPlot().setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
			index += 1;
		}

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setFillZoomRectangle(false);
		chartPanel.setPopupMenu(null);
		sn.setContent(chartPanel);
		root.setCenter(sn);
	}
}
