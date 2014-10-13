package stsc.frontend.zozka.controllers;

import java.io.IOException;
import java.net.URL;
import org.controlsfx.dialog.Dialogs;

import stsc.frontend.zozka.gui.models.ExecutionDescription;
import stsc.frontend.zozka.settings.ControllerHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SimulatorSettingsController {

	private final Stage owner;
	private final Parent gui;

	private final ObservableList<ExecutionDescription> model;

	@FXML
	private BorderPane mainPane;
	@FXML
	private TableView<ExecutionDescription> executionsTable;
	@FXML
	private TableColumn<ExecutionDescription, String> executionsNameColumn;
	@FXML
	private TableColumn<ExecutionDescription, String> algorithmsNameColumn;

	public SimulatorSettingsController(Stage owner) throws IOException {
		this.owner = owner;
		final URL location = SimulatorSettingsController.class.getResource("03_simulation_settings_pane.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		this.gui = loader.load();
		this.model = FXCollections.observableArrayList();
		initialize();
	}

	private void initialize() {
		validateGui();
		executionsTable.setItems(model);
		ControllerHelper.connectDeleteAction(owner, executionsTable, model);
		executionsNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExecutionName()));
		algorithmsNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAlgorithmName()));
	}

	public Parent getGui() {
		return gui;
	}

	private void validateGui() {
		assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file.";
		assert executionsTable != null : "fx:id=\"executionsTable\" was not injected: check your FXML file.";
		assert executionsNameColumn != null : "fx:id=\"executionsNameColumn\" was not injected: check your FXML file.";
		assert algorithmsNameColumn != null : "fx:id=\"algorithmsNameColumn\" was not injected: check your FXML file.";
	}

	@FXML
	private void loadFromFile() {
		System.out.println("load");
	}

	@FXML
	private void saveToFile() {
		System.out.println("save2");
	}

	@FXML
	private void mouseClicked(MouseEvent e) {
		if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2
				&& !executionsTable.getSelectionModel().getSelectedItems().isEmpty()) {
			editExecution();
		}
	}

	@FXML
	private void addNewExecution() {
		ExecutionDescription ed = null;
		try {
			final CreateAlgorithmController controller = new CreateAlgorithmController(owner);
			ed = controller.getExecutionDescription();
		} catch (IOException e) {
			Dialogs.create().showException(e);
		}
		if (ed != null) {
			model.add(ed);
		}
	}

	private void editExecution() {
		final int index = executionsTable.getSelectionModel().getSelectedIndex();
		final ExecutionDescription ed = executionsTable.getSelectionModel().getSelectedItem();
		try {
			final CreateAlgorithmController controller = new CreateAlgorithmController(owner, ed);
			final ExecutionDescription newEd = controller.getExecutionDescription();
			if (newEd != null) {
				model.set(index, newEd);
			}
		} catch (IOException exception) {
			Dialogs.create().showException(exception);
		}
	}
}
