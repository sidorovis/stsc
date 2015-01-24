package stsc.frontend.zozka.dialogs;

import org.controlsfx.dialog.Dialogs;

import javafx.application.Application;
import javafx.stage.Stage;

public class VisualTestDatePickerDialog extends Application {

	@Override
	public void start(Stage parent) throws Exception {
		final DatePickerDialog dialog = new DatePickerDialog(parent);
		dialog.centerOnScreen();
		dialog.showAndWait();
		Dialogs.create().owner(parent).title("Result").masthead(String.valueOf(dialog.isOk())).showInformation();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestDatePickerDialog.class, (java.lang.String[]) null);
	}

}
