package stsc.frontend.zozka.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Queue;
import java.util.ResourceBundle;

import org.controlsfx.dialog.Dialogs;

import stsc.frontend.zozka.gui.models.ExecutionDescription;
import stsc.frontend.zozka.gui.models.SimulationType;
import stsc.frontend.zozka.gui.models.SimulationsDescription;
import stsc.frontend.zozka.settings.ControllerHelper;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.yahoo.YahooFileStockStorage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

public class CreateSettingsController implements Initializable {

	private static final String DATE_VALIDATION_MESSAGE = "From date should be less or equal then To date";
	private static final String DATAFEED_PATH_VALIDATION_MESSAGE = "Datafeed path is incorrect";

	private Stage stage;
	private boolean valid;

	private SimulationsDescription model = new SimulationsDescription();

	private SimulationType simulationType;

	@FXML
	private Label datafeedLabel;
	@FXML
	private Button chooseDatafeedButton;

	@FXML
	private DatePicker fromDate;
	@FXML
	private DatePicker toDate;

	@FXML
	private Button addExecutionButton;

	@FXML
	private TableView<ExecutionDescription> executionsTable;
	@FXML
	private TableColumn<ExecutionDescription, String> executionNameColumn;
	@FXML
	private TableColumn<ExecutionDescription, String> algorithmNameColumn;

	@FXML
	private Button createGridSettingsButton;
	@FXML
	private Button createGeneticSettingsButton;

	public CreateSettingsController(final Stage owner) throws IOException {
		stage = new Stage();
		valid = false;
		final URL location = CreateSettingsController.class.getResource("01_create_settings.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		final Parent gui = loader.load();
		stage.initOwner(owner);
		stage.initModality(Modality.WINDOW_MODAL);
		final Scene scene = new Scene(gui);
		stage.setScene(scene);
		stage.setMinHeight(480);
		stage.setMinWidth(640);
		stage.setTitle("Create Simulator Settings");
		stage.centerOnScreen();
	}

	@Override
	public void initialize(final URL url, final ResourceBundle rb) {
		validateGui();
		connectTableForExecutions();

		setDefaultValues();
		setOnChooseDatafeedButton();
		setOnAddExecutionButton();
		setOnCreateGeneticSettingsButton();
		setOnCreateGridSettingsButton();
	}

	private void validateGui() {
		assert chooseDatafeedButton != null : "fx:id=\"chooseDatafeedButton\" was not injected: check your FXML file.";
		assert datafeedLabel != null : "fx:id=\"datafeedLabel\" was not injected: check your FXML file.";

		assert fromDate != null : "fx:id=\"fromDate\" was not injected: check your FXML file.";
		assert toDate != null : "fx:id=\"toDate\" was not injected: check your FXML file.";

		assert addExecutionButton != null : "fx:id=\"addExecutionButton\" was not injected: check your FXML file.";

		assert executionsTable != null : "fx:id=\"executionsTable\" was not injected: check your FXML file.";
		assert executionNameColumn != null : "fx:id=\"executionNameColumn\" was not injected: check your FXML file.";
		assert algorithmNameColumn != null : "fx:id=\"algorithmNameColumn\" was not injected: check your FXML file.";

		assert createGridSettingsButton != null : "fx:id=\"createGridSettingsButton\" was not injected: check your FXML file.";
		assert createGeneticSettingsButton != null : "fx:id=\"createGeneticSettingsButton\" was not injected: check your FXML file.";
	}

	private void connectTableForExecutions() {
		executionsTable.setOnMouseClicked(e -> {
			if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
				final int index = executionsTable.getSelectionModel().getSelectedIndex();
				final ExecutionDescription ed = executionsTable.getSelectionModel().getSelectedItem();
				try {
					final CreateAlgorithmController controller = new CreateAlgorithmController(stage, ed);
					final ExecutionDescription newEd = controller.getExecutionDescription();
					if (newEd != null) {
						model.getExecutionDescriptions().set(index, newEd);
					}
				} catch (IOException | BadParameterException exception) {
					Dialogs.create().showException(exception);
				}
			}
		});
		ControllerHelper.connectDeleteAction(stage, executionsTable, model.getExecutionDescriptions());

		executionNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExecutionName()));
		algorithmNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAlgorithmName()));
	}

	private void setDefaultValues() {
		setDatafeed("./test_data");

		fromDate.setValue(LocalDate.of(1990, 1, 1));
		toDate.setValue(LocalDate.of(2010, 1, 1));
	}

	private void setOnChooseDatafeedButton() {
		chooseDatafeedButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				final DirectoryChooser dc = new DirectoryChooser();
				dc.setTitle("Datafeed folder");
				final File f = dc.showDialog(stage);
				if (f != null && f.isDirectory()) {
					setDatafeed(f.getAbsolutePath());
				}
			}
		});
	}

	private void setOnAddExecutionButton() {
		addExecutionButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ExecutionDescription ed = null;
				try {
					final CreateAlgorithmController controller = new CreateAlgorithmController(stage);
					ed = controller.getExecutionDescription();
				} catch (IOException | BadParameterException e) {
					Dialogs.create().showException(e);
				}
				if (ed != null) {
					model.getExecutionDescriptions().add(ed);
				}
			}
		});

	}

	private void setOnCreateGeneticSettingsButton() {
		createGeneticSettingsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				simulationType = SimulationType.GENETIC;
				handleClose();
			}
		});
	}

	private void setOnCreateGridSettingsButton() {
		createGridSettingsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				simulationType = SimulationType.GRID;
				handleClose();
			}
		});
	}

	protected void handleClose() {
		final LocalDate fromDateData = fromDate.getValue();
		final LocalDate toDateData = toDate.getValue();
		if (fromDateData.isAfter(toDateData)) {
			Dialogs.create().owner(stage).title("Validation Error")
					.masthead(fromDateData.toString() + " is after " + toDateData.toString()).message(DATE_VALIDATION_MESSAGE).showError();
		} else {
			startCheckAndLoadDatafeed();
		}
	}

	private static class ProgressBarTask extends Task<Integer> {

		private final Queue<String> queue;
		private final int initialSize;

		ProgressBarTask(YahooFileStockStorage stockStorage) {
			queue = stockStorage.getTasks();
			initialSize = queue.size();
		}

		@Override
		protected Integer call() throws Exception {
			int iterations = initialSize - queue.size();
			while (!queue.isEmpty()) {
				updateProgress(iterations, initialSize);
				iterations = initialSize - queue.size();
				Thread.sleep(300);
			}
			return iterations;
		}
	}

	private static class OnSuccessEventHandler implements EventHandler<WorkerStateEvent> {

		final CreateSettingsController controller;

		OnSuccessEventHandler(CreateSettingsController controller) {
			this.controller = controller;
		}

		@Override
		public void handle(WorkerStateEvent event) {
			controller.setValid();
		}
	}

	private static class OnFailureEventHandler implements EventHandler<WorkerStateEvent> {

		final CreateSettingsController controller;

		OnFailureEventHandler(CreateSettingsController controller) {
			this.controller = controller;
		}

		@Override
		public void handle(WorkerStateEvent event) {
			controller.setInvalid();
		}
	}

	protected boolean startCheckAndLoadDatafeed() {
		final File datafeedFile = new File(model.getDatafeedPath());
		if (!(new File(datafeedFile + "/data").isDirectory()))
			return false;
		if (!(new File(datafeedFile + "/filtered_data").isDirectory()))
			return false;
		try {
			loadDatafeed(datafeedFile);
		} catch (ClassNotFoundException | IOException e) {
			Dialogs.create().showException(e);
			return false;
		}
		return true;
	}

	private void loadDatafeed(File datafeedFile) throws ClassNotFoundException, IOException {
		final YahooFileStockStorage yfStockStorage = new YahooFileStockStorage(datafeedFile + "/data", datafeedFile + "/filtered_data");
		final ProgressBarTask task = new ProgressBarTask(yfStockStorage);
		Dialogs.create().owner(stage).title("Stock Storage loading").message("Loading...").showWorkerProgress(task);
		new Thread(task).start();
		model.setStockStorage(yfStockStorage);

		task.setOnSucceeded(new OnSuccessEventHandler(this));
		final OnFailureEventHandler failure = new OnFailureEventHandler(this);
		task.setOnFailed(failure);
		task.setOnCancelled(failure);
	}

	private Date createDate(LocalDate date) {
		return new Date(date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
	}

	protected void setValid() {
		valid = true;
		model.setPeriod(createDate(fromDate.getValue()), createDate(toDate.getValue()));
		stage.close();
	}

	protected void setInvalid() {
		Dialogs.create().owner(stage).title("Validation Error").masthead("Datafeed folder: " + model.getDatafeedPath() + " is invalid.")
				.message(DATAFEED_PATH_VALIDATION_MESSAGE).showError();
	}

	private void setDatafeed(String datafeed) {
		model.setDatafeedPath(datafeed);
		datafeedLabel.setText("Datafeed: " + datafeed);
	}

	public boolean isValid() {
		stage.showAndWait();
		return valid;
	}

	public SimulationType getSimulationType() {
		return simulationType;
	}

	public SimulationsDescription getModel() {
		return model;
	}
}
