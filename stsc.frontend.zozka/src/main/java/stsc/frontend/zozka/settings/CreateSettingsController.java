package stsc.frontend.zozka.settings;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Queue;
import java.util.ResourceBundle;

import org.controlsfx.dialog.Dialogs;

import stsc.common.storage.StockStorage;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

public class CreateSettingsController implements Initializable {

	private static final String DATE_VALIDATION_MESSAGE = "From date should be less or equal then To date";
	private static final String DATAFEED_PATH_VALIDATION_MESSAGE = "Datafeed path is incorrect";

	private Stage createSettingsStage;

	private String datafeedPath = "./";
	private StockStorage stockStorage;
	private LocalDate fromDateData;
	private LocalDate toDateData;

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
	private TableColumn<ExecutionDescription.ExecutionName, TableView<ExecutionDescription>> executionNameColumn;
	@FXML
	private TableColumn<ExecutionDescription.AlgorithmName, TableView<ExecutionDescription>> algorithmNameColumn;

	@FXML
	private Button createSettingsButton;

	public static CreateSettingsController create(final Stage stage) throws IOException {
		final Stage createSettingsStage = new Stage();
		final URL location = Zozka.class.getResource("01_create_settings.fxml");
		final FXMLLoader loader = new FXMLLoader();
		final Parent createSettingsParent = loader.load(location.openStream());
		createSettingsStage.initOwner(stage);
		createSettingsStage.initModality(Modality.WINDOW_MODAL);
		final CreateSettingsController createSettingsController = loader.getController();
		createSettingsController.setStage(createSettingsStage);
		final Scene scene = new Scene(createSettingsParent);
		createSettingsStage.setScene(scene);
		createSettingsStage.setMinHeight(800);
		createSettingsStage.setMinWidth(640);
		createSettingsStage.setTitle("Create Simulator Settings");
		createSettingsStage.centerOnScreen();
		createSettingsStage.showAndWait();
		return createSettingsController;
	}

	public void setStage(Stage createSettingsStage) {
		this.createSettingsStage = createSettingsStage;
	}

	@Override
	public void initialize(final URL url, final ResourceBundle rb) {
		assert chooseDatafeedButton != null : "fx:id=\"chooseDatafeedButton\" was not injected: check your FXML file.";
		assert datafeedLabel != null : "fx:id=\"datafeedLabel\" was not injected: check your FXML file.";

		assert fromDate != null : "fx:id=\"fromDate\" was not injected: check your FXML file.";
		assert toDate != null : "fx:id=\"toDate\" was not injected: check your FXML file.";

		assert executionsTable != null : "fx:id=\"executionsTable\" was not injected: check your FXML file.";
		executionsTable.setItems(model);

		assert createSettingsButton != null : "fx:id=\"createSettingsButton\" was not injected: check your FXML file.";

		setDefaultValues();
		setOnChooseButton();
		setOnCreateSettingsButton();
	}

	private void setDefaultValues() {
		setDatafeed("./test_data");

		fromDateData = LocalDate.of(1990, 1, 1);
		toDateData = LocalDate.of(2010, 1, 1);
		fromDate.setValue(fromDateData);
		toDate.setValue(toDateData);
	}

	private void setOnChooseButton() {
		chooseDatafeedButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				final DirectoryChooser dc = new DirectoryChooser();
				dc.setTitle("Datafeed folder");
				final File f = dc.showDialog(createSettingsStage);
				if (f != null && f.isDirectory()) {
					setDatafeed(f.getAbsolutePath());
				}
			}
		});
	}

	private void setOnCreateSettingsButton() {
		createSettingsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				fromDateData = fromDate.getValue();
				toDateData = toDate.getValue();
				if (fromDateData.isAfter(toDateData)) {
					Dialogs.create().owner(createSettingsStage).title("Validation Error")
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
			return false;
		}
		return true;
	}

	private void loadDatafeed(File datafeedFile) throws ClassNotFoundException, IOException {
		final YahooFileStockStorage yfStockStorage = new YahooFileStockStorage(datafeedFile + "/data", datafeedFile + "/filtered_data");
		final ProgressBarTask task = new ProgressBarTask(yfStockStorage);
		Dialogs.create().owner(createSettingsStage).title("Stock Storage loading").message("Loading...").showWorkerProgress(task);
		new Thread(task).start();
		stockStorage = yfStockStorage;

		task.setOnSucceeded(new OnSuccessEventHandler(this));
		final OnFailureEventHandler failure = new OnFailureEventHandler(this);
		task.setOnFailed(failure);
		task.setOnCancelled(failure);
	}

	protected void setValid() {
		valid = true;
		createSettingsStage.close();
	}

	protected void setInvalid() {
		Dialogs.create().owner(createSettingsStage).title("Validation Error").masthead("Datafeed folder: " + datafeedPath + " is invalid.")
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

	public LocalDate getFromDateData() {
		return fromDateData;
	}

	public LocalDate getToDateData() {
		return toDateData;
	}

}
