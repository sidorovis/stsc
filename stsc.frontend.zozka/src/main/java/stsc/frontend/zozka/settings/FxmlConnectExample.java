package stsc.frontend.zozka.settings;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FxmlConnectExample extends Application {

	private CreateSettingsController createSettingsController;

	@Override
	public void start(final Stage stage) {
		try {
			if (createSimulatorSettingsDialog(stage)) {
				createMainWindow(stage);
			} else {
				Platform.exit();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Platform.exit();
		}
	}

	private void createMainWindow(Stage stage) {
		stage.show();

	}

	private boolean createSimulatorSettingsDialog(final Stage stage)
			throws IOException {
		final Stage createSettingsStage = new Stage();
		final URL location = FxmlConnectExample.class
				.getResource("01_create_settings.fxml");
		final FXMLLoader loader = new FXMLLoader();
		final Parent createSettingsParent = loader.load(location.openStream());
		createSettingsStage.initOwner(stage);
		createSettingsStage.initModality(Modality.WINDOW_MODAL);
		createSettingsController = loader.getController();
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

	public static void main(String[] args) {
		Application.launch(FxmlConnectExample.class, (java.lang.String[]) null);
	}
}
