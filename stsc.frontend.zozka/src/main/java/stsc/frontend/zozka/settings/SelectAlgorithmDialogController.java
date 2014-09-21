package stsc.frontend.zozka.settings;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import org.controlsfx.dialog.Dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SelectAlgorithmDialogController implements Initializable {

	private static String EXECUTION_NAME_PATTERN = "([\\w_])+";

	private Stage chooseAlgoStage;
	private Set<String> algorithmList;

	private String chosedExecutionName = null;
	private String chosedAlgorithmName = null;

	@FXML
	private TextField inputField;

	private final ObservableList<String> model = FXCollections.observableArrayList();
	@FXML
	private ListView<String> algorithms;

	public static ExecutionDescription create(Stage stage, Set<String> algoNames) throws IOException {
		final Stage chooseAlgoStage = new Stage();
		final URL location = Zozka.class.getResource("01_select_algorithm_dialog.fxml");
		final FXMLLoader loader = new FXMLLoader();
		final Parent createSettingsParent = loader.load(location.openStream());
		chooseAlgoStage.initOwner(stage);
		chooseAlgoStage.initModality(Modality.WINDOW_MODAL);
		final SelectAlgorithmDialogController controller = loader.getController();
		controller.setStage(chooseAlgoStage);
		controller.setAlgorithmsList(algoNames);
		final Scene scene = new Scene(createSettingsParent);
		chooseAlgoStage.setScene(scene);
		chooseAlgoStage.setMinHeight(800);
		chooseAlgoStage.setMinWidth(640);
		chooseAlgoStage.setTitle("Create Simulator Settings");
		chooseAlgoStage.centerOnScreen();
		chooseAlgoStage.showAndWait();
		return controller.getChoosedExecution();
	}

	private ExecutionDescription getChoosedExecution() {
		if (chosedExecutionName == null || chosedAlgorithmName == null) {
			return null;
		}
		return new ExecutionDescription(chosedExecutionName, chosedAlgorithmName);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		assert inputField != null : "fx:id=\"inputField\" was not injected: check your FXML file.";
		assert algorithms != null : "fx:id=\"algorithms\" was not injected: check your FXML file.";

		inputField.setText("Future and that functionality will come.");
		algorithms.setItems(model);
		addDoubleClickOnListAction();
		addAutoFilter();
	}

	private void addAutoFilter() {
		// inputField.setOnKeyTyped(new EventHandler<KeyEvent>() {
		// @Override
		// public void handle(KeyEvent keyEvent) {
		// // if (keyEvent.getCode().equals(KeyCode.UNDEFINED)) {
		// // setDataToModel(keyEvent.getCharacter());
		// // }
		// }
		// });
	}

	private void addEnterProcess() {
		// chooseAlgoStage.addEventHandler(KeyEvent.KEY_TYPED, new
		// EventHandler<KeyEvent>() {
		// @Override
		// public void handle(KeyEvent event) {
		// if (event.getCode().equals(KeyCode.ENTER)) {
		// inputField.setText(algorithms.getSelectionModel().getSelectedItem());
		// setChoosedAlgorithmName(algorithms.getSelectionModel().getSelectedItem());
		// }
		// }
		// });
	}

	private void addDoubleClickOnListAction() {
		algorithms.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
					setChoosedAlgorithmName(algorithms.getSelectionModel().getSelectedItem());
				}
				// if (mouseEvent.getButton().equals(MouseButton.PRIMARY) &&
				// mouseEvent.getClickCount() == 1) {
				// //
				// inputField.setText(algorithms.getSelectionModel().getSelectedItem());
				// // setDataToModel("");
				// }
			}
		});
	}

	protected void setChoosedAlgorithmName(String selectedItem) {
		this.chosedAlgorithmName = selectedItem;
		final Optional<String> value = Dialogs.create().owner(chooseAlgoStage).title("Enter execution name").masthead("Execution name")
				.showTextInput("default_algorithm_name");
		if (value.isPresent()) {
			if (value.get().matches(EXECUTION_NAME_PATTERN)) {
				this.chosedExecutionName = value.get();
				this.chooseAlgoStage.close();
			}
		}
	}

	private void setStage(Stage chooseAlgoStage) {
		this.chooseAlgoStage = chooseAlgoStage;
		addEnterProcess();
	}

	private void setAlgorithmsList(Set<String> algorithmList) {
		this.algorithmList = algorithmList;
		setDataToModel("");
	}

	private void setDataToModel(String key) {
		final String filter = "" ; // TODO inputField.getText() + key;
		model.clear();
		if (filter == null || filter.isEmpty()) {
			for (String algoName : algorithmList) {
				model.add(algoName);
			}
		} else {
			for (String algoName : algorithmList) {
				if (algoName.indexOf(filter) != -1) {
					model.add(algoName);
				}
			}
		}
	}
}
