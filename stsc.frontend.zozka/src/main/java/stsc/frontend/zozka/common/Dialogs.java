package stsc.frontend.zozka.common;

import javafx.stage.Modality;
import javafx.stage.Stage;

public class Dialogs {

	public static void information(Stage parent, String text) {
		final Stage dialogStage = new Stage();
		dialogStage.initOwner(parent);
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.showAndWait();
		// final Scene dialogScene = new Scene();
		// dialogStage.setScene(dialogScene);
		//
		// dialogStage.setScene(new Scene(VBoxBuilder.create().children(new
		// Text(text), new Button("Ok.")).alignment(Pos.CENTER)
		// .padding(new Insets(5)).build()));
		dialogStage.show();
	}
}
