package stsc.frontend.zozka.settings;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class FxmlConnectExample extends Application {

	@Override
	public void start(final Stage stage) {
		try {
			final URL location = FxmlConnectExample.class.getResource("01_create_settings.fxml");
			final BorderPane page = (BorderPane) FXMLLoader.load(location);
			final Scene scene = new Scene(page);
			stage.setScene(scene);
			stage.setMinHeight(640);
			stage.setMinWidth(610);
			stage.setTitle("FXML is Simple");
			stage.centerOnScreen();
			stage.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Application.launch(FxmlConnectExample.class, (java.lang.String[]) null);
	}
}
