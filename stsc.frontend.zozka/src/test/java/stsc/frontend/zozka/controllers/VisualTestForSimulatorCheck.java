package stsc.frontend.zozka.controllers;

import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class VisualTestForSimulatorCheck extends Application {

	private final SplitPane splitPane = new SplitPane();
	private final TabPane tabPane = new TabPane();

	private PeriodAndDatafeedController periodAndDatafeedController;
	private TextArea textArea = new TextArea();

	private void fillTopPart(Stage stage) throws IOException {
		final BorderPane pane = new BorderPane();
		periodAndDatafeedController = new PeriodAndDatafeedController(stage);
		pane.setTop(periodAndDatafeedController.getGui());
		pane.setCenter(textArea);

		final HBox hbox = new HBox();

		final Button calculateSeries = new Button("Calculate Series");
		calculateSeries.setOnAction(e -> {
			calculateSeries();
		});

		final Button calculateEquityButton = new Button("Calculate Equity");
		calculateEquityButton.setOnAction(e -> {
			calculateEquity();
		});

		hbox.getChildren().add(calculateSeries);
		hbox.getChildren().add(calculateEquityButton);

		hbox.setAlignment(Pos.CENTER);
		pane.setBottom(hbox);
		BorderPane.setAlignment(hbox, Pos.CENTER);
		splitPane.getItems().add(pane);
	}

	private void fillBottomPart(Stage stage) {
		splitPane.getItems().add(tabPane);
	}

	private void calculateSeries() {
		
	}

	private void calculateEquity() {
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
		tabPane.getSelectionModel().select(tab);
	}

	@Override
	public void start(Stage stage) throws Exception {
		splitPane.setOrientation(Orientation.VERTICAL);
		stage.setMinWidth(800);
		stage.setMinHeight(800);
		fillTopPart(stage);
		fillBottomPart(stage);

		final Scene scene = new Scene(splitPane);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestForSimulatorCheck.class, args);
	}

}
