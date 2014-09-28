package stsc.frontend.zozka.settings;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

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

}
