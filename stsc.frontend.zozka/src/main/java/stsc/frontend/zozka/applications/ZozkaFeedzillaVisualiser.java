package stsc.frontend.zozka.applications;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import stsc.frontend.zozka.panes.FeedzillaArticlesPane;

public class ZozkaFeedzillaVisualiser extends Application {

	private FeedzillaArticlesPane feedzillaArticlesPane;
	private Stage owner;

	public ZozkaFeedzillaVisualiser() throws IOException {
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.owner = stage;
		feedzillaArticlesPane = new FeedzillaArticlesPane(owner);
		owner.setScene(new Scene(feedzillaArticlesPane.getMainPane()));
		owner.show();
	}

	public static void main(String[] args) {
		Application.launch(ZozkaFeedzillaVisualiser.class, args);
	}
}
