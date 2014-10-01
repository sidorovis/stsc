package stsc.frontend.zozka.settings;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;

import stsc.common.stocks.Stock;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PresimulationCheckController implements Initializable {

	private Stage stage;
	private SimulationsDescription simulationsDescription;

	private DatasetForStock chartDataset;
	private SwingNode sn = new SwingNode();

	@FXML
	private BorderPane centralPane;

	PresimulationCheckController(final Stage stage, SimulationsDescription simulationsDescription) throws IOException {
		this.stage = new Stage();
		this.simulationsDescription = simulationsDescription;
		final URL location = Zozka.class.getResource("02_presimulation_check.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		final Parent gui = loader.load();
		this.stage.initOwner(stage);
		this.stage.initModality(Modality.WINDOW_MODAL);
		final Scene scene = new Scene(gui);
		this.stage.setScene(scene);
		this.stage.setMinHeight(480);
		this.stage.setMinWidth(640);
		this.stage.setTitle("Presimulation Check");
		this.stage.centerOnScreen();
		this.stage.showAndWait();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		assert centralPane != null : "fx:id=\"centralPane\" was not injected: check your FXML file.";
		final Stock aapl = simulationsDescription.getStockStorage().getStock("aapl");
		chartDataset = new DatasetForStock(aapl);

		final JFreeChart chart = ChartFactory.createCandlestickChart("aapl", "", "", chartDataset, false);
		chart.getPlot().setBackgroundPaint(Color.white);
		chart.getXYPlot().setDomainGridlinePaint(Color.black);
		chart.getXYPlot().setRangeGridlinePaint(Color.black);

		TimeSeriesCollection otherDataSet = new TimeSeriesCollection();
		TimeSeries ts1 = new TimeSeries("Series 1");
		ts1.add(new Year(2014), 150);
		ts1.add(new Year(2013), 100);
		otherDataSet.addSeries(ts1);
		chart.getXYPlot().setDataset(1, otherDataSet);
		chart.getXYPlot().mapDatasetToRangeAxis(1, 0);
		XYItemRenderer renderer2 = new XYLineAndShapeRenderer();
		chart.getXYPlot().setRenderer(1, renderer2);
		chart.getXYPlot().setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setFillZoomRectangle(false);
		chartPanel.setPopupMenu(null);
		sn.setContent(chartPanel);
		centralPane.setCenter(sn);
	}
}
