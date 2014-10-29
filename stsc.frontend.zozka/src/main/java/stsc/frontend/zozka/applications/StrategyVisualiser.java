package stsc.frontend.zozka.applications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.controlsfx.dialog.Dialogs;
import stsc.common.BadSignalException;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.storage.SignalsStorage;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.controllers.PeriodAndDatafeedController;
import stsc.frontend.zozka.panes.EquityPane;
import stsc.frontend.zozka.panes.StockViewPane;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.trading.TradeProcessorInit;
import javafx.application.Application;
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

public class StrategyVisualiser extends Application {

	private Stage owner;
	private final SplitPane splitPane = new SplitPane();
	private final TabPane tabPane = new TabPane();

	private PeriodAndDatafeedController periodAndDatafeedController;
	private TextArea textArea = new TextArea();

	private void fillTopPart() throws IOException {
		final BorderPane pane = new BorderPane();
		periodAndDatafeedController = new PeriodAndDatafeedController(owner);
		pane.setTop(periodAndDatafeedController.getGui());
		pane.setCenter(textArea);

		final HBox hbox = new HBox();

		final Button calculateSeries = new Button("Calculate Series");
		calculateSeries.setOnAction(e -> {
			try {
				calculateSeries();
			} catch (Exception exc) {
				Dialogs.create().showException(exc);
			}
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

	private void fillBottomPart() {
		splitPane.getItems().add(tabPane);
	}

	private void calculateSeries() throws InterruptedException {
		periodAndDatafeedController.loadStockStorage(h -> {
			calculateSeries(periodAndDatafeedController.getStockStorage());
		});
	}

	private Optional<String> chooseStock(final StockStorage stockStorage) {
		final Set<String> stockNames = stockStorage.getStockNames();
		final ArrayList<String> stockNamesList = new ArrayList<>();
		stockNamesList.addAll(stockNames);
		Collections.sort(stockNamesList);
		return Dialogs.create().owner(owner).title("Choose Stock Name").masthead("Stock name").message(null).showChoices(stockNamesList);
	}

	private void calculateSeries(final StockStorage stockStorage) {
		if (stockStorage == null) {
			return;
		}
		final Optional<String> stockName = chooseStock(stockStorage);
		if (!stockName.isPresent()) {
			return;
		}
		final Stock stock = stockStorage.getStock(stockName.get());
		addSeriesForStock(stockStorage, stock);
	}

	private void addSeriesForStock(StockStorage stockStorage, Stock stock) {
		try {
			final FromToPeriod period = periodAndDatafeedController.getPeriod();

			final TradeProcessorInit init = new TradeProcessorInit(stockStorage, period, textArea.getText());
			final List<String> executionsName = init.generateOutForStocks();
			final SimulatorSettings settings = new SimulatorSettings(0, init);

			final Set<String> stockNames = new HashSet<String>(Arrays.asList(new String[] { stock.getName() }));
			final Simulator simulator = new Simulator(settings, stockNames);
			final SignalsStorage signalsStorage = simulator.getSignalsStorage();

			final StockViewPane stockViewPane = new StockViewPane(owner, stock, period, executionsName, signalsStorage);
			final Tab tab = new Tab();
			tab.setText(stock.getName());
			tab.setContent(stockViewPane.getMainPane());
			tabPane.getTabs().add(tab);
			tabPane.getSelectionModel().select(tab);

		} catch (Exception e) {
			Dialogs.create().showException(e);
		}
	}

	private void calculateEquity() {
		periodAndDatafeedController.loadStockStorage(h -> {
			calculateEquity(periodAndDatafeedController.getStockStorage());
		});
	}

	private void calculateEquity(StockStorage stockStorage) {
		if (stockStorage == null) {
			return;
		}
		try {
			final FromToPeriod period = periodAndDatafeedController.getPeriod();

			final TradeProcessorInit init = new TradeProcessorInit(stockStorage, period, textArea.getText());
			final SimulatorSettings settings = new SimulatorSettings(0, init);

			final Simulator simulator = new Simulator(settings);
			addEquityTab(simulator, period);
		} catch (BadAlgorithmException | BadSignalException | IOException e) {
			Dialogs.create().showException(e);
		}
	}

	private void addEquityTab(Simulator simulator, FromToPeriod period) throws IOException {
		final EquityPane equityPane = new EquityPane(owner, simulator.getStatistics(), period);
		final Tab tab = new Tab();
		tab.setText("S:" + simulator.getStatistics().getAvGain());
		tab.setContent(equityPane.getMainPane());
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tab);
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.owner = stage;
		splitPane.setOrientation(Orientation.VERTICAL);
		stage.setMinWidth(800);
		stage.setMinHeight(480);
		fillTopPart();
		fillBottomPart();

		final Scene scene = new Scene(splitPane);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Application.launch(StrategyVisualiser.class, args);
	}

}
