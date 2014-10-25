package stsc.frontend.zozka.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class PeriodAndDatafeedController {

	private Stage owner;
	private final Parent gui;

	@FXML
	private Label datafeedPath;
	@FXML
	private DatePicker fromPeriod;
	@FXML
	private DatePicker toPeriod;

	public PeriodAndDatafeedController(final Stage owner) throws IOException {
		this.owner = owner;
		final URL location = SimulatorSettingsController.class.getResource("03_period_and_datafeed_pane.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		this.gui = loader.load();
		initialize();
	}

	public Parent getGui() {
		return gui;
	}

	private void initialize() {
		validateGui();
		fromPeriod.setValue(LocalDate.of(1990, 1, 1));
		toPeriod.setValue(LocalDate.of(2010, 12, 31));
	}

	private void validateGui() {
		assert datafeedPath != null : "fx:id=\"datafeedPath\" was not injected: check your FXML file.";
		assert fromPeriod != null : "fx:id=\"fromPeriod\" was not injected: check your FXML file.";
		assert toPeriod != null : "fx:id=\"toPeriod\" was not injected: check your FXML file.";
	}

	@FXML
	private void datafeedEdit(final MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton().equals(MouseButton.PRIMARY)) {
			final String path = datafeedPath.getText();
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
				datafeedPath.setText(result.getAbsolutePath());
			}
		}
	}
}
