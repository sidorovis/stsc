package stsc.frontend.zozka.panes.internal;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class ProgressWithStopPane extends BorderPane {

	private final HBox pane = new HBox();
	private final ProgressIndicator indicator = new ProgressIndicator();
	private final Button stopSearchButton = new Button();

	public ProgressWithStopPane() {
		this(true);
	}

	public ProgressWithStopPane(boolean withStopButton) {
		this.indicator.setPrefSize(40, 40);
		this.indicator.setProgress(0.0);
		this.setCenter(pane);
		stopSearchButton.setText("Stop Search");
		pane.getChildren().add(indicator);
		if (withStopButton) {
			pane.getChildren().add(stopSearchButton);
		}
		pane.setAlignment(Pos.CENTER);
		BorderPane.setAlignment(pane, Pos.CENTER);
	}

	public void setOnStopButtonAction(Runnable action) {
		stopSearchButton.setOnAction(e -> {
			action.run();
			stopSearchButton.setText("Stopped");
			stopSearchButton.setDisable(true);
		});
	}

	public void enableStopButton() {
		stopSearchButton.setDisable(false);
	}

	public void show() {
		this.setVisible(true);
	}

	public void hide() {
		this.setVisible(false);
	}

	public void setIndicatorProgress(double percent) {
		indicator.setProgress(percent);
	}
}
