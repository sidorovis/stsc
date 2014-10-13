package stsc.frontend.zozka.controllers;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestSimulatorSettingsPane extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		SimulatorSettingsPane pane = new SimulatorSettingsPane(stage);
		final Scene scene = new Scene(pane.getGui());
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Application.launch(TestSimulatorSettingsPane.class, args);
	}
}
