package stsc.frontend.zozka.settings;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.controlsfx.dialog.Dialogs;

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
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateSettingsController implements Initializable {

	private static final String DATE_VALIDATION_MESSAGE = "From date should be less or equal then To date";

	private Stage createSettingsStage;
	private Stage stage;

	private String datafeedPath = "./";
	private LocalDate fromDateData;
	private LocalDate toDateData;

	private boolean valid = false;

	@FXML
	private Button chooseDatafeedButton;
	@FXML
	private Label datafeedLabel;
	@FXML
	private DatePicker fromDate;
	@FXML
	private DatePicker toDate;
	@FXML
	private Button createSettingsButton;

	public static boolean create(final Stage stage) throws IOException {
		final Stage createSettingsStage = new Stage();
		final URL location = Zozka.class.getResource("01_create_settings.fxml");
		final FXMLLoader loader = new FXMLLoader();
		final Parent createSettingsParent = loader.load(location.openStream());
		createSettingsStage.initOwner(stage);
		createSettingsStage.initModality(Modality.WINDOW_MODAL);
		final CreateSettingsController createSettingsController = loader.getController();
		createSettingsController.setStage(createSettingsStage, stage);
		final Scene scene = new Scene(createSettingsParent);
		createSettingsStage.setScene(scene);
		createSettingsStage.setMinHeight(660);
		createSettingsStage.setMinWidth(615);
		createSettingsStage.setTitle("Simulator Settings");
		createSettingsStage.centerOnScreen();
		createSettingsStage.showAndWait();
		return createSettingsController.isValid();
	}

	public void setStage(Stage createSettingsStage, Stage stage) {
		this.createSettingsStage = createSettingsStage;
		this.stage = stage;
	}

	@Override
	public void initialize(final URL url, final ResourceBundle rb) {
		assert chooseDatafeedButton != null : "fx:id=\"chooseDatafeedButton\" was not injected: check your FXML file.";
		assert datafeedLabel != null : "fx:id=\"datafeedLabel\" was not injected: check your FXML file.";
		assert fromDate != null : "fx:id=\"fromDate\" was not injected: check your FXML file.";
		assert toDate != null : "fx:id=\"toDate\" was not injected: check your FXML file.";
		assert createSettingsButton != null : "fx:id=\"toDate\" was not injected: check your FXML file.";

		setDatafeed("D:\\dev\\java\\StscData");

		fromDateData = LocalDate.of(2010, 1, 2);
		toDateData = LocalDate.of(2010, 1, 1);
		fromDate.setValue(fromDateData);
		toDate.setValue(toDateData);

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
					setValid();
					createSettingsStage.close();
				}
			}
		});
	}

	protected void setValid() {
		valid = true;
	}

	private void setDatafeed(String datafeed) {
		datafeedPath = datafeed;
		datafeedLabel.setText("Datafeed: " + datafeed);
	}

	public boolean isValid() {
		return valid;
	}

	public String getDatafeedPath() {
		return datafeedPath;
	}

	public LocalDate getFromDateData() {
		return fromDateData;
	}

	public LocalDate getToDateData() {
		return toDateData;
	}

}
