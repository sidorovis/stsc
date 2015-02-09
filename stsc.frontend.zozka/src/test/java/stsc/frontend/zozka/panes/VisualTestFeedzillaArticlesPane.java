package stsc.frontend.zozka.panes;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VisualTestFeedzillaArticlesPane extends Application {

	@Override
	public void start(Stage parent) throws Exception {
		final FeedzillaArticlesPane equityPane = new FeedzillaArticlesPane(parent);
		final Scene scene = new Scene(equityPane.getMainPane());
		parent.setScene(scene);
		parent.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestFeedzillaArticlesPane.class, (java.lang.String[]) null);
	}

}
