package stsc.frontend.zozka.settings;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import org.controlsfx.dialog.Dialogs;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PresimulationCheckController implements Initializable {

	private Stage stage;
	private SimulationType simulationType;
	private SimulationsDescription simulationsDescription;

	@FXML
	private Label executionRepresentation;

	@FXML
	private Button showPriceChartForStock;
	@FXML
	private Button showStockChartWithOnStock;

	@FXML
	private Label datafeedPath;

	PresimulationCheckController(final Stage stage, SimulationsDescription simulationsDescription, SimulationType simulationType)
			throws IOException {
		this.stage = new Stage();
		this.simulationsDescription = simulationsDescription;
		this.simulationType = simulationType;
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
		validateGui();
		setLabels();
		connectShowPriceChartForStockButton();
		connectShowPriceChartWithOnStockButton();
	}

	private void setLabels() {
		final long listSize = getListSize();
		executionRepresentation.setText(simulationType.toString() + " size: " + listSize);
		datafeedPath.setText("Datafeed: " + simulationsDescription.getDatafeedPath());
	}

	private void connectShowPriceChartForStockButton() {
		showPriceChartForStock.setOnAction(e -> {
			final Optional<String> choosedName = selectStockDialog();
			if (choosedName.isPresent()) {
				showStockDialog(choosedName.get());
			}
		});
	}

	private void connectShowPriceChartWithOnStockButton() {

		showStockChartWithOnStock.setOnAction(e -> {
			try {
				final Optional<String> choosedName = selectStockDialog();
				if (!choosedName.isPresent()) {
					return;
				}
				final Optional<SimulatorSettings> simulationSettings = createSimulatorSettings();
				if (!simulationSettings.isPresent()) {
					return;
				}
				final Set<String> stockNames = new HashSet<String>(Arrays.asList(new String[] { choosedName.get() }));
				final Simulator simulator = new Simulator(simulationSettings.get(), stockNames);
				simulator.getStatistics();
				simulator.getSignalsStorage();
			} catch (Exception exception) {
				Dialogs.create().showException(exception);
			}
		});

	}

	private Optional<SimulatorSettings> createSimulatorSettings() throws IOException, BadAlgorithmException {
		final CreateSimulationSettingsController controller = new CreateSimulationSettingsController(stage,
				simulationsDescription.getPeriod(), simulationsDescription.getStockStorage());
		return controller.getSettings();
	}

	private Optional<String> selectStockDialog() {
		final Set<String> stockNames = simulationsDescription.getStockStorage().getStockNames();
		final ArrayList<String> stockNamesList = new ArrayList<>();
		stockNamesList.addAll(stockNames);
		Collections.sort(stockNamesList);
		return Dialogs.create().title("Choose Stock Name").masthead("Choost stock name").message(null).showChoices(stockNamesList);
	}

	private void showStockDialog(String stockName) {
		final Stock stock = simulationsDescription.getStockStorage().getStock(stockName);
		new ShowStockView(stock);
	}

	private long getListSize() {
		if (simulationType.equals(SimulationType.GENETIC)) {
			return simulationsDescription.getGenetic().size();
		} else
			return simulationsDescription.getGrid().size();
	}

	private void validateGui() {
		assert executionRepresentation != null : "fx:id=\"executionRepresentation\" was not injected: check your FXML file.";

		assert showPriceChartForStock != null : "fx:id=\"showPriceChartForStock\" was not injected: check your FXML file.";
		assert showStockChartWithOnStock != null : "fx:id=\"showStockChartWithOnStock\" was not injected: check your FXML file.";

		assert datafeedPath != null : "fx:id=\"datafeedPath\" was not injected: check your FXML file.";
	}
}
