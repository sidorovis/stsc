package stsc.frontend.zozka.panes;

import java.io.File;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import stsc.common.FromToPeriod;
import stsc.frontend.zozka.gui.models.SimulationType;
import stsc.frontend.zozka.models.SimulatorSettingsModel;
import stsc.yahoo.YahooFileStockStorage;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

public class VisualTestStrategiesPane extends Application {

	final SplitPane chartPane = new SplitPane();

	private JFreeChart addChartPane() {
		final JFreeChart chart = ChartFactory.createTimeSeriesChart("", "Time", "Value", null, true, false, false);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setFillZoomRectangle(false);
		chartPanel.setPopupMenu(null);
		SwingNode sn = new SwingNode();
		sn.setContent(chartPanel);
		chartPane.getItems().add(sn);
		return chart;
	}

	@Override
	public void start(Stage parent) throws Exception {
		chartPane.setOrientation(Orientation.VERTICAL);
		chartPane.setDividerPosition(0, 0.5);
		final JFreeChart chart = addChartPane();
		final Scene scene = new Scene(chartPane);

		final YahooFileStockStorage yfss = new YahooFileStockStorage("./test_data/data", "./test_data/filtered_data");
		yfss.waitForLoad();

		final FromToPeriod period = new FromToPeriod("01-01-1990", "31-12-2010");
		SimulatorSettingsModel model = new SimulatorSettingsModel();
		model.loadFromFile(new File("./test_data/strategy_selector/size_2280"));

		final StrategiesPane sp = new StrategiesPane(parent, period, model, yfss, chart, SimulationType.GENETIC);
		chartPane.getItems().add(sp);
		parent.setScene(scene);
		parent.setMinHeight(800);
		parent.setMinWidth(800);
		parent.setWidth(800);
		parent.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestStrategiesPane.class, (java.lang.String[]) null);
	}
}
