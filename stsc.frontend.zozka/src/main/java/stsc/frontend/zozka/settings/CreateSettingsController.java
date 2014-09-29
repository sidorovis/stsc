package stsc.frontend.zozka.settings;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Queue;
import java.util.ResourceBundle;

import org.controlsfx.dialog.Dialogs;

import stsc.common.storage.StockStorage;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

public class CreateSettingsController implements Initializable {

	private static final String DATE_VALIDATION_MESSAGE = "From date should be less or equal then To date";
	private static final String DATAFEED_PATH_VALIDATION_MESSAGE = "Datafeed path is incorrect";

	private Stage stage;

	private String datafeedPath = "./";
	private StockStorage stockStorage;

	private boolean valid = false;

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

	private ObservableList<ExecutionDescription> model = FXCollections.observableArrayList();
	@FXML
	private TableView<ExecutionDescription> executionsTable;
	@FXML
	private TableColumn<ExecutionDescription, String> executionNameColumn;
	@FXML
	private TableColumn<ExecutionDescription, String> algorithmNameColumn;

	@FXML
	private Button createSettingsButton;

	public static CreateSettingsController create(final Stage stage) throws IOException {
		final Stage thisStage = new Stage();
		final URL location = Zozka.class.getResource("01_create_settings.fxml");
		final FXMLLoader loader = new FXMLLoader();
		final Parent gui = loader.load(location.openStream());
		thisStage.initOwner(stage);
		thisStage.initModality(Modality.WINDOW_MODAL);
		final CreateSettingsController controller = loader.getController();
		controller.setStage(thisStage);
		final Scene scene = new Scene(gui);
		thisStage.setScene(scene);
		thisStage.setMinHeight(800);
		thisStage.setMinWidth(640);
		thisStage.setTitle("Create Simulator Settings");
		thisStage.centerOnScreen();
		thisStage.showAndWait();
		return controller;
	}

	public void setStage(Stage createSettingsStage) {
		this.stage = createSettingsStage;
	}

	@Override
	public void initialize(final URL url, final ResourceBundle rb) {
		validateGui();
		connectTableForExecutions();

		setDefaultValues();
		setOnChooseDatafeedButton();
		setOnAddExecutionButton();
		setOnCreateSettingsButton();
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

		assert createSettingsButton != null : "fx:id=\"createSettingsButton\" was not injected: check your FXML file.";
	}

	private void connectTableForExecutions() {
		ControllerHelper.connectDeleteAction(stage, executionsTable, model);

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
					ed = CreateAlgorithmController.create(stage);
				} catch (IOException | BadParameterException e) {
					Dialogs.create().showException(e);
				}
				if (ed != null) {
					model.add(ed);
				}
			}
		});

	}

	private void setOnCreateSettingsButton() {
		createSettingsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				final LocalDate fromDateData = fromDate.getValue();
				final LocalDate toDateData = toDate.getValue();
				if (fromDateData.isAfter(toDateData)) {
					Dialogs.create().owner(stage).title("Validation Error")
							.masthead(fromDateData.toString() + " is after " + toDateData.toString()).message(DATE_VALIDATION_MESSAGE)
							.showError();
				} else {
					checkAndLoadDatafeed();
				}
			}
		});
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

	protected boolean checkAndLoadDatafeed() {
		final File datafeedFile = new File(datafeedPath);
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
		stockStorage = yfStockStorage;

		task.setOnSucceeded(new OnSuccessEventHandler(this));
		final OnFailureEventHandler failure = new OnFailureEventHandler(this);
		task.setOnFailed(failure);
		task.setOnCancelled(failure);
	}

	protected void setValid() {
		valid = true;
		stage.close();
	}

	protected void setInvalid() {
		Dialogs.create().owner(stage).title("Validation Error").masthead("Datafeed folder: " + datafeedPath + " is invalid.")
				.message(DATAFEED_PATH_VALIDATION_MESSAGE).showError();
	}

	private void setDatafeed(String datafeed) {
		datafeedPath = datafeed;
		datafeedLabel.setText("Datafeed: " + datafeed);
	}

	public boolean isValid() {
		return valid;
	}

	public StockStorage getStockStorage() {
		return stockStorage;
	}

}
