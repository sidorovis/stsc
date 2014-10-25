package stsc.frontend.zozka.controllers;

import javafx.application.Application;
import javafx.stage.Stage;
import stsc.frontend.zozka.gui.models.ExecutionDescription;

public class VisualTestCreateAlgorithmControllerApplication extends Application {

	public VisualTestCreateAlgorithmControllerApplication() {

	}

	@Override
	public void start(Stage parent) throws Exception {
		CreateAlgorithmController controller = new CreateAlgorithmController(parent);
		final ExecutionDescription ed = controller.getExecutionDescription();
		if (ed != null) {
			controller = new CreateAlgorithmController(parent, ed);
			controller.getExecutionDescription();
		}
		parent.close();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestCreateAlgorithmControllerApplication.class, (java.lang.String[]) null);

	}
}
