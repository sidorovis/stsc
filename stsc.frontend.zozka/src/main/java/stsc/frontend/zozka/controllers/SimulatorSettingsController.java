package stsc.frontend.zozka.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import stsc.frontend.zozka.gui.models.ExecutionDescription;
import stsc.frontend.zozka.models.SimulatorSettingsModel;
import stsc.frontend.zozka.settings.ControllerHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SimulatorSettingsController {

	private final Stage owner;
	private final Parent gui;

	private final SimulatorSettingsModel model;

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
		this.model = new SimulatorSettingsModel();
		final URL location = SimulatorSettingsController.class.getResource("03_simulation_settings_pane.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		this.gui = loader.load();
		initialize();
	}

	private void initialize() {
		validateGui();
		executionsTable.setItems(model.getModel());
		ControllerHelper.connectDeleteAction(owner, executionsTable, model.getModel());
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
		if (!model.isEmpty()) {
			final Action response = Dialogs.create().owner(owner).title("Are you sure?")
					.masthead("Model have " + model.size() + " execution descriptions")
					.message("Do you want to erase them and load from file?").showConfirm();
			if (response != Dialog.Actions.YES) {
				return;
			}
		}
		final FileChooser dc = new FileChooser();
		dc.setTitle("File To Load");
		final File f = dc.showOpenDialog(owner);
		try {
			if (f != null) {
				if (!(f.exists() && f.isFile())) {
					Dialogs.create().owner(owner).title("Simulator Settings Load Error")
							.masthead("File can't be loaded (" + f.getAbsolutePath() + ")").message("Please choose another one")
							.showError();

				}
				model.loadFromFile(f);
			}
		} catch (Exception e) {
			Dialogs.create().owner(owner).showException(e);
		}
	}

	@FXML
	private void saveToFile() {
		final FileChooser dc = new FileChooser();
		dc.setTitle("File To Save");
		final File f = dc.showSaveDialog(owner);
		try {
			if (f != null) {
				if (f.exists() && !f.canWrite()) {
					Dialogs.create().owner(owner).title("Simulator Settings Save Error")
							.masthead("File can't be writen (" + f.getAbsolutePath() + ")").message("Please choose another one")
							.showError();
					return;
				}
				model.saveToFile(f);
			}
		} catch (IOException e) {
			Dialogs.create().owner(owner).showException(e);
		}
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
		Optional<ExecutionDescription> ed = Optional.empty();
		try {
			final CreateAlgorithmController controller = new CreateAlgorithmController(owner);
			ed = controller.getExecutionDescription();
		} catch (IOException e) {
			Dialogs.create().showException(e);
		}
		if (ed.isPresent()) {
			model.add(ed.get());
		}
	}

	private void editExecution() {
		final int index = executionsTable.getSelectionModel().getSelectedIndex();
		final ExecutionDescription ed = executionsTable.getSelectionModel().getSelectedItem();
		try {
			final CreateAlgorithmController controller = new CreateAlgorithmController(owner, ed);
			final Optional<ExecutionDescription> newEd = controller.getExecutionDescription();
			if (newEd.isPresent()) {
				model.set(index, newEd.get());
			}
		} catch (IOException exception) {
			Dialogs.create().showException(exception);
		}
	}

	public SimulatorSettingsModel getModel() {
		return model;
	}
}
