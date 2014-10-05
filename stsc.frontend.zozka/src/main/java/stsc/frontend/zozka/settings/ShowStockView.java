package stsc.frontend.zozka.settings;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import stsc.common.FromToPeriod;
import stsc.common.signals.Signal;
import stsc.common.signals.StockSignal;
import stsc.common.stocks.Stock;
import stsc.common.storage.SignalsStorage;
import stsc.signals.DoubleSignal;
import stsc.storage.ExecutionsStorage;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ShowStockView {

	private final Stage stage;
	private DatasetForStock chartDataset;
	private TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
	private SwingNode sn = new SwingNode();

	ShowStockView(Stock stock, FromToPeriod period) {
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

		final JFreeChart chart = ChartFactory.createCandlestickChart("", "", "", chartDataset, false);
		chart.getPlot().setBackgroundPaint(Color.white);
		chart.getXYPlot().setDomainGridlinePaint(Color.black);
		chart.getXYPlot().setRangeGridlinePaint(Color.black);

		for (String executionName : executionsName) {
			final String outName = ExecutionsStorage.outNameFor(executionName);
			final int size = signalsStorage.getIndexSize(stock.getName(), outName);
			final TimeSeries ts = new TimeSeries(executionName);
			for (int i = 0; i < size; ++i) {
				final Signal<? extends StockSignal> s = signalsStorage.getStockSignal(stock.getName(), outName, i);
				ts.add(new Day(s.getDate()), s.getSignal(DoubleSignal.class).value);
			}
			timeSeriesCollection.addSeries(ts);
		}

		chart.getXYPlot().setDataset(1, timeSeriesCollection);
		chart.getXYPlot().mapDatasetToRangeAxis(1, 0);
		final XYItemRenderer seriesRenderer = new XYLineAndShapeRenderer();
		chart.getXYPlot().setRenderer(1, seriesRenderer);
		chart.getXYPlot().setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setFillZoomRectangle(false);
		chartPanel.setPopupMenu(null);
		sn.setContent(chartPanel);
		root.setCenter(sn);
	}
}
