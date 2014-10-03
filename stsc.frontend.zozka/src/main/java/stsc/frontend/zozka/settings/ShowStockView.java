package stsc.frontend.zozka.settings;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import stsc.common.stocks.Stock;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ShowStockView {

	private final Stage stage;
	private DatasetForStock chartDataset;
	private SwingNode sn = new SwingNode();

	ShowStockView(Stock stock) {
		this.stage = new Stage();
		this.stage.initModality(Modality.WINDOW_MODAL);
		this.setCenterWidget(stock);
		this.stage.setMinHeight(380);
		this.stage.setMinWidth(480);
		this.stage.setHeight(380);
		this.stage.setWidth(480);
		this.stage.setTitle("Stock: " + stock.getName());
		this.stage.centerOnScreen();
		this.stage.showAndWait();
	}

	private void setCenterWidget(final Stock stock) {
		final BorderPane root = new BorderPane();
		final Scene scene = new Scene(root);
		this.stage.setScene(scene);
		this.chartDataset = new DatasetForStock(stock);

		final JFreeChart chart = ChartFactory.createCandlestickChart("", "", "", chartDataset, false);
		chart.getPlot().setBackgroundPaint(Color.white);
		chart.getXYPlot().setDomainGridlinePaint(Color.black);
		chart.getXYPlot().setRangeGridlinePaint(Color.black);

		// TimeSeriesCollection otherDataSet = new TimeSeriesCollection();
		// TimeSeries ts1 = new TimeSeries("Series 1");
		// ts1.add(new Year(2014), 150);
		// ts1.add(new Year(2013), 100);
		// otherDataSet.addSeries(ts1);
		// chart.getXYPlot().setDataset(1, otherDataSet);
		// chart.getXYPlot().mapDatasetToRangeAxis(1, 0);
		// XYItemRenderer renderer2 = new XYLineAndShapeRenderer();
		// chart.getXYPlot().setRenderer(1, renderer2);
		// chart.getXYPlot().setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setFillZoomRectangle(false);
		chartPanel.setPopupMenu(null);
		sn.setContent(chartPanel);
		root.setCenter(sn);
	}
}
