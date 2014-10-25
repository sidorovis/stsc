package stsc.frontend.zozka.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
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
import javafx.stage.FileChooser;
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
		this.model = FXCollections.observableArrayList();
		final URL location = SimulatorSettingsController.class.getResource("03_simulation_settings_pane.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		this.gui = loader.load();
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
				loadModelFromFile(f);
			}
		} catch (Exception e) {
			Dialogs.create().owner(owner).showException(e);
		}
	}

	private void loadModelFromFile(File f) throws FileNotFoundException, IOException, ClassNotFoundException {
		try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(f))) {
			final int size = is.readInt();
			model.clear();
			for (int i = 0; i < size; ++i) {
				final ExecutionDescription ed = ExecutionDescription.createForLoadFromFile();
				ed.readExternal(is);
				model.add(ed);
			}
		}
	}

	@FXML
	private void saveToFile() {
		final FileChooser dc = new FileChooser();
		dc.setTitle("File To Save");
		final File f = dc.showSaveDialog(owner);
		try {
			if (f != null) {
				if (!f.canWrite()) {
					Dialogs.create().owner(owner).title("Simulator Settings Save Error")
							.masthead("File can't be writen (" + f.getAbsolutePath() + ")").message("Please choose another one")
							.showError();
					return;
				}
				saveModelToFile(f);
			}
		} catch (IOException e) {
			Dialogs.create().owner(owner).showException(e);
		}
	}

	private void saveModelToFile(File f) throws FileNotFoundException, IOException {
		try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f))) {
			os.writeInt(model.size());
			for (ExecutionDescription executionDescription : model) {
				executionDescription.writeExternal(os);
			}
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
