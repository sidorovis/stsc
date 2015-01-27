package stsc.frontend.zozka.dialogs;

import java.time.LocalDate;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DatePickerDialog extends Stage {

	private boolean pressed = false;
	private final BorderPane bp = new BorderPane();
	private final DatePicker dp = new DatePicker(LocalDate.now());
	private final Button ok = new Button("Ok");

	public DatePickerDialog(String title, Stage owner, LocalDate from) {
		this(title, owner);
		dp.setValue(from);
	}

	public DatePickerDialog(String title, Stage owner) {
		super(StageStyle.UTILITY);
		ok.setDefaultButton(true);
		ok.setOnAction((e) -> {
			pressed = true;
			this.hide();
		});
		bp.setCenter(dp);
		bp.setBottom(ok);
		BorderPane.setAlignment(ok, Pos.CENTER);
		ok.setAlignment(Pos.CENTER);
		this.setScene(new Scene(bp, 280, 80));
		this.setTitle(title);
	}

	public boolean isOk() {
		return pressed;
	}

	public LocalDate getDate() {
		return dp.getValue();
	}

}
