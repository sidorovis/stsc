package stsc.frontend.zozka.dialogs;

import java.time.LocalDate;

import javafx.application.Application;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

public class VisualTestDatePickerDialog extends Application {

	@Override
	public void start(Stage parent) throws Exception {
		final DatePickerDialog dialog = new DatePickerDialog("Date Choose Title", parent, LocalDate.of(1990, 1, 1));
		dialog.centerOnScreen();
		dialog.showAndWait();
		Dialogs.create().owner(parent).title("Result").masthead(String.valueOf(dialog.isOk())).showInformation();
	}

	public static void main(String[] args) {
		Application.launch(VisualTestDatePickerDialog.class, (java.lang.String[]) null);
	}

}
