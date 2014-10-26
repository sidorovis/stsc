package stsc.frontend.zozka.controllers;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class VisualTestPeriodAndDatafeedController extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		final PeriodAndDatafeedController controller = new PeriodAndDatafeedController(stage);
		BorderPane pane = new BorderPane();
		pane.setCenter(controller.getGui());
		final Button save = new Button("Test Save");
		pane.setBottom(save);
		final Scene scene = new Scene(pane);
		stage.setScene(scene);
		stage.show();
		save.setOnAction(eh -> {
			System.out.println(controller.getPeriod());
			controller.loadStockStorage(hs -> {
				System.out.println(hs);
				System.out.println(controller.getStockStorage());
			});
		});

	}

	public static void main(String[] args) {
		Application.launch(VisualTestPeriodAndDatafeedController.class, args);
	}

}
