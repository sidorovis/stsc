package stsc.frontend.zozka.dialogs;

import javafx.application.Application;
import javafx.stage.Stage;

public class VisualTestTextFieldDialog extends Application {

	@Override
	public void start(Stage parent) throws Exception {
		new TextFieldDialog(parent, 14, "hello world\nresult").show();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestTextFieldDialog.class, (java.lang.String[]) null);
	}
}
