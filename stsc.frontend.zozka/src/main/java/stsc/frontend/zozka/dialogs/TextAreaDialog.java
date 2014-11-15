package stsc.frontend.zozka.dialogs;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialog;

public class TextAreaDialog extends Dialog {

	private final TextArea textArea;
	private final BorderPane borderPane = new BorderPane();

	public TextAreaDialog(Stage owner, String title, String value) {
		super(owner, title);
		this.textArea = new TextArea(value);
		this.getWindow().setWidth(600);
		this.getWindow().setHeight(600);
		this.getWindow().centerOnScreen();
		this.setContent(borderPane);
		borderPane.setCenter(textArea);
		borderPane.setBottom(new Label(""));
	}

}
