package stsc.frontend.zozka.applications;

import java.io.File;
import java.io.IOException;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ZozkaDatafeedChecker extends Application {

	private Stage owner;

	@FXML
	private final BorderPane borderPane = new BorderPane();
	@FXML
	private final Label datafeedPathLabel = new Label();
	private String datafeedPath;

	public ZozkaDatafeedChecker() {
	}

	@Override
	public void start(final Stage owner) throws Exception {
		this.owner = owner;
		final Scene scene = new Scene(borderPane);
		borderPane.setTop(datafeedPathLabel);
		owner.setScene(scene);
		owner.show();
		loadDatafeed();
	}

	@FXML
	public void datafeedEdit(final MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
			chooseFolder();
		}
	}

	private void chooseFolder() {
		final String path = datafeedPathLabel.getText();
		final File f = new File(path);

		final Action response = Dialogs.create().owner(owner).title("Datafeed Path").masthead("Do you want to change datafeed path?")
				.message("Current path is: " + path).showConfirm();
		if (response != Dialog.Actions.YES) {
			return;
		}
		final DirectoryChooser dc = new DirectoryChooser();
		if (f.exists()) {
			dc.setInitialDirectory(f);
		}
		final File result = dc.showDialog(owner);
		if (result != null && result.isDirectory()) {
			datafeedPathLabel.setText(result.getAbsolutePath());
			try {
				loadDatafeed();
			} catch (IOException e) {
				Dialogs.create().showException(e);
			}
		}
	}

	private void loadDatafeed() throws IOException {
		if (datafeedPath == null || datafeedPath != datafeedPathLabel.getText()) {
			datafeedPath = datafeedPathLabel.getText();

		}
	}

	public static void main(String[] args) {
		Application.launch(ZozkaDatafeedChecker.class, args);
	}
}
