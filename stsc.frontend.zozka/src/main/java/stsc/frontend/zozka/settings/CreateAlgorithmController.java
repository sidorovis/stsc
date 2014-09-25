package stsc.frontend.zozka.settings;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateAlgorithmController implements Initializable {

	private Stage stage;

	@FXML
	private ComboBox<String> algorithmType;
	@FXML
	private ComboBox<String> algorithmClass;
	@FXML
	private TextField executionName;

	private ObservableList<NumberAlgorithmParameter> numberModel = FXCollections.observableArrayList();
	@FXML
	private TableView<NumberAlgorithmParameter> numberTable;
	@FXML
	private TableColumn<NumberAlgorithmParameter, String> numberParName;
	@FXML
	private TableColumn<NumberAlgorithmParameter, String> numberParType;
	@FXML
	private TableColumn<NumberAlgorithmParameter, String> numberParFrom;
	@FXML
	private TableColumn<NumberAlgorithmParameter, String> numberParStep;
	@FXML
	private TableColumn<NumberAlgorithmParameter, String> numberParTo;

	private ObservableList<TextAlgorithmParameter> textModel = FXCollections.observableArrayList();
	@FXML
	private TableView<TextAlgorithmParameter> textTable;
	@FXML
	private TableColumn<TextAlgorithmParameter, String> textParName;
	@FXML
	private TableColumn<TextAlgorithmParameter, String> textParType;
	@FXML
	private TableColumn<TextAlgorithmParameter, String> textParDomen;

	@FXML
	private Button addParameter;
	@FXML
	private Button createExecution;

	public static ExecutionDescription create(final Stage parentStage) throws IOException {
		final Stage thisStage = new Stage();
		final URL location = Zozka.class.getResource("01_create_algorithm.fxml");
		final FXMLLoader loader = new FXMLLoader();
		final Parent gui = loader.load(location.openStream());
		thisStage.initOwner(parentStage);
		thisStage.initModality(Modality.WINDOW_MODAL);
		final CreateAlgorithmController controller = loader.getController();
		controller.setStage(thisStage);
		final Scene scene = new Scene(gui);
		scene.getStylesheets().add(Zozka.class.getResource("01_create_algorithm.css").toExternalForm());
		thisStage.setScene(scene);
		thisStage.setMinHeight(800);
		thisStage.setMinWidth(640);
		thisStage.setTitle("Create Algorithm Settings");
		thisStage.centerOnScreen();
		thisStage.showAndWait();
		return null;
	}

	private void setStage(Stage thisStage) {
		this.stage = thisStage;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		validateGui();
		connectTableForNumber();
		connectTableForText();
	}

	private void validateGui() {
		assert algorithmType != null : "fx:id=\"algorithmType\" was not injected: check your FXML file.";
		assert algorithmClass != null : "fx:id=\"algorithmClass\" was not injected: check your FXML file.";
		assert executionName != null : "fx:id=\"executionName\" was not injected: check your FXML file.";

		assert numberTable != null : "fx:id=\"numberParameters\" was not injected: check your FXML file.";
		assert numberParName != null : "fx:id=\"numberParName\" was not injected: check your FXML file.";
		assert numberParType != null : "fx:id=\"numberParType\" was not injected: check your FXML file.";
		assert numberParFrom != null : "fx:id=\"numberParFrom\" was not injected: check your FXML file.";
		assert numberParStep != null : "fx:id=\"numberParStep\" was not injected: check your FXML file.";
		assert numberParTo != null : "fx:id=\"numberParTo\" was not injected: check your FXML file.";

		assert textTable != null : "fx:id=\"textParameters\" was not injected: check your FXML file.";
		assert textParName != null : "fx:id=\"textParName\" was not injected: check your FXML file.";
		assert textParType != null : "fx:id=\"textParType\" was not injected: check your FXML file.";
		assert textParDomen != null : "fx:id=\"textParDomen\" was not injected: check your FXML file.";

		assert addParameter != null : "fx:id=\"addParameter\" was not injected: check your FXML file.";
		assert createExecution != null : "fx:id=\"createExecution\" was not injected: check your FXML file.";
	}

	private void connectTableForNumber() {
		numberTable.setItems(numberModel);
		// numberTable.setRowFactory(e -> {
		// final TableRow<NumberAlgorithmParameter> answer = new TableRow<>();
		// answer.getStyleClass().add("correct");
		// return answer;
		// });
		numberParName.setCellValueFactory(new PropertyValueFactory<NumberAlgorithmParameter, String>("parameterName"));
		numberParType.setCellValueFactory(new PropertyValueFactory<NumberAlgorithmParameter, String>("type"));

		connectNumberColumn(numberParFrom, "from");
		numberParFrom.setOnEditCommit(e -> e.getRowValue().setFrom(e.getNewValue()));
		connectNumberColumn(numberParStep, "step");
		numberParStep.setOnEditCommit(e -> e.getRowValue().setStep(e.getNewValue()));
		connectNumberColumn(numberParTo, "to");
		numberParTo.setOnEditCommit(e -> e.getRowValue().setTo(e.getNewValue()));

		numberModel.add(new NumberAlgorithmParameter("asd", "sdf", 1d, 1d, 15d));
		numberModel.add(new NumberAlgorithmParameter("asd", "sdf", 1d, 1d, 15d));
		numberModel.add(new NumberAlgorithmParameter("asd", "sdf", 1d, 1d, 15d));
		numberModel.add(new NumberAlgorithmParameter("asd", "sdf", 1d, 1d, 15d));
	}

	private void connectNumberColumn(TableColumn<NumberAlgorithmParameter, String> column, String name) {
		column.setCellValueFactory(new PropertyValueFactory<NumberAlgorithmParameter, String>(name));
		column.setCellFactory(TextFieldTableCell.forTableColumn());

		// TODO validation think about
		// if (!e.getRowValue().isValid()) {
		// e.getTableView().getRowFactory().call(e.getTableView()).getStyleClass().add("error");
		// } else {
		// e.getTableView().getRowFactory().call(e.getTableView()).getStyleClass().clear();//
		// add("correct");
		// }

	}

	private void connectTableForText() {
		textTable.setItems(textModel);
		textParName.setCellValueFactory(cellData -> cellData.getValue().getParameterName());
		textParType.setCellValueFactory(cellData -> cellData.getValue().getType());

		connectTextColumn(textParDomen, "domen");

		textModel.add(new TextAlgorithmParameter("asd", "sdf", "asd, fds, grtg"));
	}

	private <T> void connectTextColumn(TableColumn<T, String> column, String name) {
		column.setCellValueFactory(new PropertyValueFactory<T, String>(name));
		column.setCellFactory(TextFieldTableCell.forTableColumn());
	}
}
