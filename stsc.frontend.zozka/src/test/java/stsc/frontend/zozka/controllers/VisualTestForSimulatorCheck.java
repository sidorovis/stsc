package stsc.frontend.zozka.controllers;

import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VisualTestForSimulatorCheck extends Application {

	private final SplitPane splitPane = new SplitPane();
	private final TabPane tabPane = new TabPane();

	private PeriodAndDatafeedController periodAndDatafeedController;
	private SimulatorSettingsController simulatorSettingsController;

	private void fillTopPane(Stage stage) throws IOException {
		BorderPane pane = new BorderPane();
		periodAndDatafeedController = new PeriodAndDatafeedController(stage);
		simulatorSettingsController = new SimulatorSettingsController(stage);
		pane.setTop(periodAndDatafeedController.getGui());
		pane.setCenter(simulatorSettingsController.getGui());
		splitPane.getItems().add(pane);
	}

	private void fillCenterPane(Stage stage) {
		addCreateSettings();
	}

	private void addCreateSettings() {
		final VBox vbox = new VBox();
		vbox.setCenterShape(true);
		final Button button = new Button("Create Settings");
		button.setOnAction(e -> {
			createSettings();
		});
		vbox.getChildren().add(button);
		vbox.getChildren().add(tabPane);
		splitPane.getItems().add(vbox);
	}

	private void createSettings() {
		final Tab tab = new Tab();
		final SwingNode sn = new SwingNode();
		final OHLCSeriesCollection timeSeries = new OHLCSeriesCollection();
		final JFreeChart chart = ChartFactory.createCandlestickChart("", "", "", timeSeries, true);
		chart.getXYPlot().setRenderer(0, new CandlestickRenderer(3));

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setFillZoomRectangle(false);
		chartPanel.setPopupMenu(null);
		sn.setContent(chartPanel);

		tab.setContent(sn);
		tabPane.getTabs().add(tab);
	}

	@Override
	public void start(Stage stage) throws Exception {
		splitPane.setOrientation(Orientation.VERTICAL);
		stage.setMinWidth(800);
		stage.setMinHeight(800);
		fillTopPane(stage);
		fillCenterPane(stage);

		final Scene scene = new Scene(splitPane);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestForSimulatorCheck.class, args);
	}

}
