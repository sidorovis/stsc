package stsc.frontend.zozka.settings;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PresimulationCheckController implements Initializable {

	private Stage stage;
	private SimulationsDescription simulationsDescription;

	public static PresimulationCheckController create(final Stage stage, SimulationsDescription simulationsDescription) throws IOException {
		final Stage thisStage = new Stage();
		final URL location = Zozka.class.getResource("02_presimulation_check.fxml");
		final FXMLLoader loader = new FXMLLoader();
		final Parent gui = loader.load(location.openStream());
		thisStage.initOwner(stage);
		thisStage.initModality(Modality.WINDOW_MODAL);
		final PresimulationCheckController controller = loader.getController();
		controller.setStage(thisStage);
		controller.setSimulationsDescription(simulationsDescription);
		final Scene scene = new Scene(gui);
		thisStage.setScene(scene);
		thisStage.setMinHeight(800);
		thisStage.setMinWidth(640);
		thisStage.setTitle("Presimulation Check");
		thisStage.centerOnScreen();
		thisStage.showAndWait();
		return controller;
	}

	public void setStage(Stage createSettingsStage) {
		this.stage = createSettingsStage;
	}

	private void setSimulationsDescription(SimulationsDescription simulationsDescription) {
		this.simulationsDescription = simulationsDescription;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

}
