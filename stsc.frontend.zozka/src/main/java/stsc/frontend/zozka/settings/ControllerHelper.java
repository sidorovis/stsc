package stsc.frontend.zozka.settings;

import java.io.File;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

public class ControllerHelper {

	public static <T> void connectDeleteAction(Stage stage, TableView<T> table, ObservableList<T> model) {
		table.setItems(model);
		table.setOnKeyReleased(e -> {
			if (e.getCode().equals(KeyCode.DELETE)) {
				final Action result = Dialogs.create().owner(stage).title("Delete Algorithm Parameter")
						.masthead("Deleting Algorithm Parameter Action").message("Are you sure to delete Algorithm Parameter?")
						.actions(Dialog.Actions.OK, Dialog.Actions.CANCEL).showConfirm();
				if (result == Dialog.Actions.OK) {
					final T elementToDelete = table.getSelectionModel().getSelectedItem();
					if (elementToDelete != null) {
						model.remove(elementToDelete);
					}
				}
			}
		});
	}

	public static boolean chooseFolder(Window owner, Label label) {
		final String path = label.getText();
		final File f = new File(path);

		final Action response = Dialogs.create().owner(owner).title("Datafeed Path").masthead("Do you want to change datafeed path?")
				.message("Current path is: " + path).showConfirm();
		if (response != Dialog.Actions.YES) {
			return false;
		}
		final DirectoryChooser dc = new DirectoryChooser();
		if (f.exists()) {
			dc.setInitialDirectory(f);
		}
		final File result = dc.showDialog(owner);
		if (result != null && result.isDirectory()) {
			label.setText(result.getAbsolutePath());
			return true;
		}
		return false;
	}

}
