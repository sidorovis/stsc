package stsc.frontend.zozka.settings;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import stsc.common.stocks.Stock;
import stsc.general.simulator.multistarter.MpIterator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfigureSettingsController implements Initializable {

	private CreateSettingsController createSettingsController;
	private Stage configureSettingsStage;
	private Stage stage;

	@FXML
	private TableView<MpIterator<?>> parameters;
	@FXML
	private TableColumn<TableView<Stock>, String> stockNames;
	@FXML
	private Button settingsDefined;

	public static ConfigureSettingsController create(final Stage stage, CreateSettingsController settingsController) throws IOException {
		final Stage configureSettingsStage = new Stage();
		final URL location = Zozka.class.getResource("02_configure_settings.fxml");
		final FXMLLoader loader = new FXMLLoader();
		final Parent configureSettingsParent = loader.load(location.openStream());
		configureSettingsStage.initOwner(stage);
		configureSettingsStage.initModality(Modality.WINDOW_MODAL);
		final ConfigureSettingsController configureSettingsController = loader.getController();
		configureSettingsController.setStage(configureSettingsStage, stage);
		configureSettingsController.setCreateSettingsController(settingsController);
		final Scene scene = new Scene(configureSettingsParent);
		configureSettingsStage.setScene(scene);
		configureSettingsStage.setMinHeight(800);
		configureSettingsStage.setMinWidth(640);
		configureSettingsStage.setTitle("Configure Simulator Settings");
		configureSettingsStage.centerOnScreen();
		configureSettingsStage.showAndWait();
		return configureSettingsController;
	}

	private void setStage(Stage configureSettingsStage, Stage stage) {
		this.configureSettingsStage = configureSettingsStage;
		this.stage = stage;
	}

	private void setCreateSettingsController(CreateSettingsController createSettingsController) {
		this.createSettingsController = createSettingsController;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		assert parameters != null : "fx:id=\"parameters\" was not injected: check your FXML file.";
		assert stockNames != null : "fx:id=\"stockNames\" was not injected: check your FXML file.";
		assert settingsDefined != null : "fx:id=\"settingsDefined\" was not injected: check your FXML file.";
		setCloseAction();
	}

	private void setCloseAction() {
		settingsDefined.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				configureSettingsStage.close();
			}
		});
	}

}
