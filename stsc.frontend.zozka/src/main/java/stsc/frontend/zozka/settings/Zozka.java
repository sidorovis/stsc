package stsc.frontend.zozka.settings;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Zozka extends Application {

	@Override
	public void start(final Stage stage) {
		try {
			if (CreateSettingsController.create(stage)) {
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

	public static void main(String[] args) {
		Application.launch(Zozka.class, (java.lang.String[]) null);
	}
}
