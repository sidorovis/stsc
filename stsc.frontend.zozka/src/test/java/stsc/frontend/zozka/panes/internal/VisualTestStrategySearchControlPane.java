package stsc.frontend.zozka.panes.internal;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class VisualTestStrategySearchControlPane extends Application {

	double percent = 0.0;

	@Override
	public void start(Stage parent) throws Exception {
		final BorderPane centerPane = new BorderPane();
		final StrategySearchControlPane pane = new StrategySearchControlPane();
		pane.setOnStopButtonAction(() -> {
			percent += 0.23;
			pane.setIndicatorProgress(percent);
		});
		centerPane.setCenter(pane);
		final Scene scene = new Scene(centerPane);

		final Button button = new Button("Enable");
		button.setOnAction(e -> {
			pane.enableStopButton();
		});
		centerPane.setTop(button);
		parent.setScene(scene);
		parent.setMinHeight(800);
		parent.setMinWidth(600);
		parent.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestStrategySearchControlPane.class, (java.lang.String[]) null);
	}
}
