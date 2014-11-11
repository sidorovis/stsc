package stsc.frontend.zozka.panes.internal;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class VisualTestProgressWithStopPane extends Application {

	double percent = 0.0;

	@Override
	public void start(Stage parent) throws Exception {
		final BorderPane centerPane = new BorderPane();
		final ProgressWithStopPane pane = new ProgressWithStopPane();
		pane.setOnStopButtonAction(() -> {
			percent += 0.23;
			pane.setIndicatorProgress(percent);
			if (percent >= 1.0) {
				pane.hide();
			}
		});
		centerPane.setBottom(pane);
		final Scene scene = new Scene(centerPane);

		final Button button = new Button("Enable");
		button.setOnAction(e -> {
			pane.show();
			pane.enableStopButton();
		});
		centerPane.setTop(button);
		parent.setScene(scene);
		parent.setMinHeight(150);
		parent.setMinWidth(150);
		parent.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestProgressWithStopPane.class, (java.lang.String[]) null);
	}
}
