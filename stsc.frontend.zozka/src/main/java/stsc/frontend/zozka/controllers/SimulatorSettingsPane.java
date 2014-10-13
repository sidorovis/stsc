package stsc.frontend.zozka.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SimulatorSettingsPane {

	private final Parent gui;

	@FXML
	private BorderPane mainPane;

	public SimulatorSettingsPane(Stage owner) throws IOException {
		final URL location = SimulatorSettingsPane.class.getResource("03_simulation_settings_pane.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		this.gui = loader.load();
	}

	public void initialize(URL location, ResourceBundle resources) {
		validateGui();
	}

	public Parent getGui() {
		return gui;
	}

	private void validateGui() {
		assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file.";

	}

	@FXML
	public void loadFromFile() {
		System.out.println("load");
	}

	@FXML
	public void saveToFile() {
		System.out.println("save2");
	}

	@FXML
	public void save() {
		System.out.println("main save");
	}

}
