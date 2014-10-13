package stsc.frontend.zozka.settings;

import java.io.IOException;

import stsc.frontend.zozka.controllers.CreateSettingsController;
import stsc.frontend.zozka.controllers.PresimulationCheckController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class ZozkaFirstExample extends Application {

	@Override
	public void start(final Stage stage) {
		try {
			mainWorkflow(stage);
		} catch (Exception ex) {
			ex.printStackTrace();
			Platform.exit();
		}
	}

	private void mainWorkflow(Stage stage) throws IOException {
		final CreateSettingsController settingsController = new CreateSettingsController(stage);
		if (!settingsController.isValid()) {
			Platform.exit();
			return;
		}
		new PresimulationCheckController(stage, settingsController.getModel(), settingsController.getSimulationType());
	}

	public static void main(String[] args) {
		Application.launch(ZozkaFirstExample.class, (java.lang.String[]) null);
	}
}
