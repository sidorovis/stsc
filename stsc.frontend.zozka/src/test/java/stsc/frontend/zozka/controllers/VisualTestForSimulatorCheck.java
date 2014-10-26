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
import javafx.stage.Stage;

public class VisualTestForSimulatorCheck extends Application {

	private final SplitPane splitPane = new SplitPane();
	private final TabPane tabPane = new TabPane();

	private PeriodAndDatafeedController periodAndDatafeedController;
	private TextArea textArea = new TextArea();

	private void fillTopPane(Stage stage) throws IOException {
		final BorderPane pane = new BorderPane();
		periodAndDatafeedController = new PeriodAndDatafeedController(stage);
		pane.setTop(periodAndDatafeedController.getGui());
		pane.setCenter(textArea);

		final Button createSettingsButton = new Button("Create Settings");
		createSettingsButton.setOnAction(e -> {
			createSettings();
		});

		pane.setBottom(createSettingsButton);
		BorderPane.setAlignment(createSettingsButton, Pos.CENTER);
		splitPane.getItems().add(pane);
	}

	private void fillCenterPane(Stage stage) {
		splitPane.getItems().add(tabPane);
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
		tabPane.getSelectionModel().select(tab);
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
