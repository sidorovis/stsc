package stsc.frontend.zozka.applications;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import stsc.frontend.zozka.panes.FeedzillaArticlesPane;

public class ZozkaFeedzillaVisualiser extends Application {

	private final FeedzillaArticlesPane feedzillaArticlesPane;
	private Stage owner;

	public ZozkaFeedzillaVisualiser() throws IOException {
		this.feedzillaArticlesPane = new FeedzillaArticlesPane();
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.owner = stage;
		owner.setScene(new Scene(feedzillaArticlesPane.getMainPane()));
		owner.show();
	}

	@FXML
	private void datafeedClicked(MouseEvent e) {
		System.out.println("click " + e.getClickCount());
	}

	public static void main(String[] args) {
		Application.launch(ZozkaFeedzillaVisualiser.class, args);
	}
}
