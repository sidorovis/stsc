package stsc.frontend.zozka.panes;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VisualTestFeedzillaCategoriesPane extends Application {

	@Override
	public void start(Stage parent) throws Exception {
		final FeedzillaCategoriesPane equityPane = new FeedzillaCategoriesPane(parent);
		final Scene scene = new Scene(equityPane);
		parent.setScene(scene);
		parent.show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestFeedzillaCategoriesPane.class, (java.lang.String[]) null);
	}

}
