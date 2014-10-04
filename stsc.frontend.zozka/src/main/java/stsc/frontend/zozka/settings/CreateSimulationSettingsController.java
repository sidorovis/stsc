package stsc.frontend.zozka.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.dialog.Dialogs;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.trading.TradeProcessorInit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateSimulationSettingsController implements Initializable {

	private boolean valid = false;
	private final Stage stage;
	private final FromToPeriod period;
	private final StockStorage stockStorage;

	@FXML
	private TextArea simulationSettingsArea;

	@FXML
	private Button loadButton;
	@FXML
	private Button saveButton;
	@FXML
	private Button createSettingsButton;

	public CreateSimulationSettingsController(final Stage stage, FromToPeriod period, StockStorage stockStorage) throws IOException {
		this.period = period;
		this.stockStorage = stockStorage;
		this.stage = new Stage();
		final URL location = Zozka.class.getResource("02_create_simulation_settings.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		final Parent gui = loader.load();
		this.stage.initOwner(stage);
		this.stage.initModality(Modality.NONE);
		final Scene scene = new Scene(gui);
		this.stage.setScene(scene);
		this.stage.setMinHeight(480);
		this.stage.setMinWidth(640);
		this.stage.setTitle("Create Simulation Settings");
		this.stage.centerOnScreen();
		this.stage.showAndWait();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		validateGui();
		connectButtons();
	}

	private void validateGui() {
		assert simulationSettingsArea != null : "fx:id=\"simulationSettingsArea\" was not injected: check your FXML file.";
		assert loadButton != null : "fx:id=\"loadButton\" was not injected: check your FXML file.";
		assert saveButton != null : "fx:id=\"saveButton\" was not injected: check your FXML file.";
		assert createSettingsButton != null : "fx:id=\"createSettingsButton\" was not injected: check your FXML file.";

	}

	private void connectButtons() {
		loadButton.setOnAction(e -> {
			final FileChooser fc = new FileChooser();
			fc.setTitle("Simulator Settings Configuration");
			final File f = fc.showOpenDialog(stage);
			if (f != null && f.isFile()) {
				try (BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()))) {
					StringBuilder sb = new StringBuilder();
					String line = br.readLine();
					while (line != null) {
						sb.append(line);
						sb.append(System.lineSeparator());
						line = br.readLine();
					}
					simulationSettingsArea.setText(sb.toString());
				} catch (IOException exception) {
					Dialogs.create().showException(exception);
				}
			}
		});
		saveButton.setOnAction(e -> {
			final FileChooser fc = new FileChooser();
			fc.setTitle("Simulator Settings Configuration");
			final File f = fc.showSaveDialog(stage);
			if (f != null) {
				try (BufferedWriter br = new BufferedWriter(new FileWriter(f.getAbsolutePath()))) {
					br.append(simulationSettingsArea.getText());
				} catch (IOException exception) {
					Dialogs.create().showException(exception);
				}
			}
		});
		createSettingsButton.setOnAction(e -> {
			valid = true;
			stage.close();
		});
	}

	public Optional<SimulatorSettings> getSettings() throws BadAlgorithmException {
		if (valid) {
			final SimulatorSettings settings = new SimulatorSettings(0, new TradeProcessorInit(stockStorage, period,
					simulationSettingsArea.getText()));
			return Optional.ofNullable(settings);
		} else {
			return Optional.empty();

		}
	}
}
