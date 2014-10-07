package stsc.frontend.zozka.settings;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Zozka extends Application {

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
		final CreateSettingsController settingsController = CreateSettingsController.create(stage);
		if (!settingsController.isValid()) {
			Platform.exit();
			return;
		}
		final PresimulationCheckController presimulationController = new PresimulationCheckController(stage, settingsController.getModel(),
				settingsController.getSimulationType());
		
		// createMainWindow(settingsController, stage);
	}

	// private void createMainWindow(CreateSettingsController
	// settingsController, Stage stage) {
	// System.out.println(settingsController.getType());
	// stage.show();
	// }

	public static void main(String[] args) {
		Application.launch(Zozka.class, (java.lang.String[]) null);
	}
}
