package stsc.frontend.zozka.dialogs;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialog;

public class TextFieldDialog extends Dialog {

	private final TextArea textArea;

	public TextFieldDialog(Stage owner, long id, String value) {
		super(owner, "Strategy: " + String.valueOf(id));
		this.textArea = new TextArea(value);
		this.getWindow().setWidth(600);
		this.getWindow().setHeight(600);
		final BorderPane borderPane = new BorderPane();
		this.setContent(borderPane);
		borderPane.setCenter(textArea);
		borderPane.setBottom(new Label(""));
	}
}
