package stsc.frontend.zozka.settings;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpString;
import stsc.general.simulator.multistarter.MpSubExecution;
import stsc.storage.AlgorithmsStorage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateAlgorithmController implements Initializable {

	private Stage stage;
	private boolean valid;

	private static String STOCK_VALUE = "Stock";
	private static String EOD_VALUE = "Eod";

	private static String INTEGER_TYPE = "Integer";
	private static String DOUBLE_TYPE = "Double";
	private static String STRING_TYPE = "String";
	private static String SUB_EXECUTIONS_TYPE = "SubExecutions";

	private static ObservableList<String> algorithmTypeModel = FXCollections.observableArrayList();
	private static List<String> typeVariants = new ArrayList<>();
	static {
		algorithmTypeModel.add(STOCK_VALUE);
		algorithmTypeModel.add(EOD_VALUE);

		typeVariants.add(INTEGER_TYPE);
		typeVariants.add(DOUBLE_TYPE);
		typeVariants.add(STRING_TYPE);
		typeVariants.add(SUB_EXECUTIONS_TYPE);
	}
	public static final Pattern parameterNamePattern = Pattern.compile("^([\\w_\\d])+$");
	public static final Pattern integerParPattern = Pattern.compile("^-?(\\d)+$");
	public static final Pattern doubleParPattern = Pattern.compile("^-?(\\d)+(\\.(\\d)+)?$");

	@FXML
	private ComboBox<String> algorithmType;
	@FXML
	private ComboBox<String> algorithmClass;
	@FXML
	private Button questionButton;
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

	public static ExecutionDescription create(final Stage parentStage) throws IOException, BadParameterException {
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
		if (controller.isValid()) {
			return createExecutionDescription(controller);
		}
		return null;
	}

	private static ExecutionDescription createExecutionDescription(CreateAlgorithmController controller) throws BadParameterException {
		final String executionName = controller.executionName.getText();
		final String algorithmName = controller.algorithmClass.getValue();
		final String executionType = controller.algorithmType.getValue();

		final ExecutionDescription ed = new ExecutionDescription(executionName, algorithmName, executionType);
		for (NumberAlgorithmParameter p : controller.numberModel) {
			if (p.getType().equals(INTEGER_TYPE)) {
				final String name = p.parameterNameProperty().get();
				final Integer from = Integer.valueOf(p.getFrom());
				final Integer to = Integer.valueOf(p.getTo());
				final Integer step = Integer.valueOf(p.getStep());
				ed.getParameters().getIntegers().add(new MpInteger(name, from, to, step));
			} else if (p.getType().equals(DOUBLE_TYPE)) {
				final String name = p.parameterNameProperty().get();
				final Double from = Double.valueOf(p.getFrom());
				final Double to = Double.valueOf(p.getTo());
				final Double step = Double.valueOf(p.getStep());
				ed.getParameters().getDoubles().add(new MpDouble(name, from, to, step));
			}
		}
		for (TextAlgorithmParameter p : controller.textModel) {
			if (p.getType().equals(STRING_TYPE)) {
				final String name = p.parameterNameProperty().get();
				final List<String> domen = parseDomen(p.domenProperty().get());
				ed.getParameters().getStrings().add(new MpString(name, domen));
			} else if (p.getType().equals(SUB_EXECUTIONS_TYPE)) {
				final String name = p.parameterNameProperty().get();
				final List<String> domen = parseDomen(p.domenProperty().get());
				ed.getParameters().getSubExecutions().add(new MpSubExecution(name, domen));
			}
		}
		return ed;
	}

	static List<String> parseDomen(String string) {
		List<String> domen = new ArrayList<>();
		for (String p : string.split(",")) {
			final String trimmed = p.trim();
			if (trimmed.length() >= 2 && trimmed.charAt(0) == '\'' && trimmed.charAt(trimmed.length() - 1) == '\'') {
				domen.add(trimmed.substring(1, trimmed.length() - 1));
			}
		}
		return domen;
	}

	private void setStage(Stage thisStage) {
		this.stage = thisStage;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		validateGui();
		connectActionsForAlgorithmType();
		connectActionsForAlgorithmClass();
		connectQuestionButton();
		connectTableForNumber();
		connectTableForText();
		connectAddParameter();
		connectCreateExecution();
	}

	private void validateGui() {
		assert algorithmType != null : "fx:id=\"algorithmType\" was not injected: check your FXML file.";
		assert algorithmClass != null : "fx:id=\"algorithmClass\" was not injected: check your FXML file.";
		assert questionButton != null : "fx:id=\"questionButton\" was not injected: check your FXML file.";
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
		valid = false;
	}

	private void connectActionsForAlgorithmType() {
		algorithmType.setItems(algorithmTypeModel);
		algorithmType.getSelectionModel().select(0);
		algorithmType.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				try {
					populateAlgorithmClassWith(newValue);
				} catch (BadAlgorithmException e) {
					Dialogs.create().showException(e);
					stage.close();
				}
			}
		});
	}

	private void connectActionsForAlgorithmClass() {
		try {
			populateAlgorithmClassWith(algorithmType.getSelectionModel().getSelectedItem());
		} catch (BadAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		}
		algorithmClass.getSelectionModel().select(0);
	}

	private void connectQuestionButton() {
		questionButton.setOnAction(e -> {
			Dialogs.create().owner(stage).title("Information").masthead(null)
					.message("To understand what is happening\nhere than please ask developer and then\nchange this text. Thanks!")
					.showInformation();
		});
	}

	protected void populateAlgorithmClassWith(String newValue) throws BadAlgorithmException {
		final ObservableList<String> model = algorithmClass.getItems();
		model.clear();
		if (newValue.equals(STOCK_VALUE)) {
			final Set<String> stockLabels = AlgorithmsStorage.getInstance().getStockLabels();
			for (String label : stockLabels) {
				model.add(label);
			}
		} else {
			final Set<String> eodLabels = AlgorithmsStorage.getInstance().getEodLabels();
			for (String label : eodLabels) {
				model.add(label);
			}
		}
		algorithmClass.getSelectionModel().select(0);
	}

	private void connectTableForNumber() {
		createDeleteAction(textTable, textModel);

		numberParName.setCellValueFactory(new PropertyValueFactory<NumberAlgorithmParameter, String>("parameterName"));
		numberParName.setCellFactory(TextFieldTableCell.forTableColumn());
		numberParType.setCellValueFactory(new PropertyValueFactory<NumberAlgorithmParameter, String>("type"));

		connectNumberColumn(numberParFrom, "from");
		numberParFrom.setOnEditCommit(e -> e.getRowValue().setFrom(e.getNewValue()));
		connectNumberColumn(numberParStep, "step");
		numberParStep.setOnEditCommit(e -> e.getRowValue().setStep(e.getNewValue()));
		connectNumberColumn(numberParTo, "to");
		numberParTo.setOnEditCommit(e -> e.getRowValue().setTo(e.getNewValue()));
	}

	private void connectNumberColumn(TableColumn<NumberAlgorithmParameter, String> column, String name) {
		column.setCellValueFactory(new PropertyValueFactory<NumberAlgorithmParameter, String>(name));
		column.setCellFactory(TextFieldTableCell.forTableColumn());

		// TODO add validation to Algorithm Controller Tables on edit
		// if (!e.getRowValue().isValid()) {
		// e.getTableView().getRowFactory().call(e.getTableView()).getStyleClass().add("error");
		// } else {
		// e.getTableView().getRowFactory().call(e.getTableView()).getStyleClass().clear();//
		// add("correct");
		// }
	}

	private void connectTableForText() {
		createDeleteAction(textTable, textModel);

		textParName.setCellValueFactory(new PropertyValueFactory<TextAlgorithmParameter, String>("parameterName"));
		textParName.setCellFactory(TextFieldTableCell.forTableColumn());
		textParType.setCellValueFactory(cellData -> cellData.getValue().getType());

		connectTextColumn(textParDomen, "domen");
	}

	private <T> void createDeleteAction(TableView<T> table, ObservableList<T> model) {
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

	private <T> void connectTextColumn(TableColumn<T, String> column, String name) {
		column.setCellValueFactory(new PropertyValueFactory<T, String>(name));
		column.setCellFactory(TextFieldTableCell.forTableColumn());
	}

	private void connectAddParameter() {
		addParameter.setOnAction(e -> {
			final Optional<String> parameterName = getParameterName();
			if (!parameterName.isPresent()) {
				return;
			}
			final Optional<String> parameterType = getParameterType();
			if (!parameterType.isPresent()) {
				return;
			}
			if (parameterType.get().equals(INTEGER_TYPE)) {
				addIntegerParameter(parameterName.get(), "0", "1", "22");
			} else if (parameterType.get().equals(DOUBLE_TYPE)) {
				addDoubleParameter(parameterName.get(), "0.0", "1.0", "22.0");
			} else if (parameterType.get().equals(STRING_TYPE)) {
				addStringParameter(parameterName.get());
			} else if (parameterType.get().equals(SUB_EXECUTIONS_TYPE)) {
				addSubExecutionParameter(parameterName.get());
			}
		});
	}

	private Optional<String> getParameterName() {
		Optional<String> parameterName = Optional.empty();
		parameterName = Dialogs.create().owner(stage).title("Enter Parameter Name").masthead("Parameter name:").message("Enter: ")
				.showTextInput("ParameterName");
		if (parameterName.isPresent()) {
			if (!parameterNamePattern.matcher(parameterName.get()).matches()) {
				Dialogs.create().owner(stage).title("Bad Parameter Name").masthead("Parameter name not match pattern.")
						.message("Parameter name should contain only letters, numbers and '_' symbol.").showError();
				return Optional.empty();
			}
			if (parameterNameExists(parameterName.get())) {
				Dialogs.create().owner(stage).title("Bad Parameter Name").masthead("Parameter name already exists.")
						.message("You could add only one parameter (one for both for number or test tables).").showError();
				return Optional.empty();
			}
		}
		return parameterName;
	}

	private boolean parameterNameExists(String parameterName) {
		for (NumberAlgorithmParameter p : numberModel) {
			if (p.parameterNameProperty().get().equals(parameterName)) {
				return true;
			}
		}
		for (TextAlgorithmParameter p : textModel) {
			if (p.parameterNameProperty().get().equals(parameterName)) {
				return true;
			}
		}
		return false;
	}

	private Optional<String> getParameterType() {
		return Dialogs.create().owner(stage).title("Choose type for parameter").masthead(null).message("Type define parameter domen: ")
				.showChoices(typeVariants);
	}

	private void addIntegerParameter(String parameterName, String defaultFrom, String defaultStep, String defaultTo) {
		final String from = readIntegerParameter(defaultFrom);
		if (from == null) {
			return;
		}
		final String step = readIntegerParameter(defaultStep);
		if (step == null) {
			return;
		}
		final String to = readIntegerParameter(defaultTo);
		if (to == null) {
			return;
		}
		numberModel.add(new NumberAlgorithmParameter(parameterName, INTEGER_TYPE, integerParPattern, from, step, to));
	}

	private String readIntegerParameter(final String defaultValue) {
		final Optional<String> integerParameter = Dialogs.create().owner(stage).title("Integer Parameter").masthead("Enter From")
				.message("From: ").showTextInput(defaultValue);
		if (integerParameter.isPresent() && !integerParPattern.matcher(integerParameter.get()).matches()) {
			Dialogs.create().owner(stage).title("Integer Parameter").masthead("Please insert integer")
					.message("Integer is a number (-)?([0-9])+").showError();
			return null;
		}
		return integerParameter.get();
	}

	private void addDoubleParameter(String parameterName, String defaultFrom, String defaultStep, String defaultTo) {
		final String from = readDoubleParameter(defaultFrom);
		if (from == null) {
			return;
		}
		final String step = readDoubleParameter(defaultStep);
		if (step == null) {
			return;
		}
		final String to = readDoubleParameter(defaultTo);
		if (to == null) {
			return;
		}
		numberModel.add(new NumberAlgorithmParameter(parameterName, DOUBLE_TYPE, doubleParPattern, from, step, to));
	}

	private String readDoubleParameter(final String defaultValue) {
		final Optional<String> doubleParameter = Dialogs.create().owner(stage).title("Double Parameter").masthead("Enter From")
				.message("From: ").showTextInput(defaultValue);
		if (doubleParameter.isPresent() && !doubleParPattern.matcher(doubleParameter.get()).matches()) {
			Dialogs.create().owner(stage).title("Double Parameter").masthead("Please insert double")
					.message("Double is a number (-)?([0-9])+(.[0-9]+)?").showError();
			return null;
		}
		return doubleParameter.get();
	}

	private void addStringParameter(String parameterName) {
		ArrayList<String> values = new ArrayList<>();
		while (true) {
			final Optional<String> stringValue = Dialogs.create().owner(stage).title("String Parameter")
					.masthead("Hack: add several divided by ','.\nPress 'Cancel' to finish enter.").message("Enter string value: ")
					.showTextInput("");
			if (stringValue.isPresent()) {
				values.add(stringValue.get());
			} else {
				break;
			}
		}
		String domen = "'";
		for (int i = 0; i < values.size(); ++i) {
			domen += values.get(i);
			if (i < values.size() - 1) {
				domen += "', '";
			}
		}
		domen += "'";
		textModel.add(new TextAlgorithmParameter(parameterName, STRING_TYPE, domen));
	}

	private void addSubExecutionParameter(String parameterName) {
		ArrayList<String> values = new ArrayList<>();
		while (true) {
			final Optional<String> stringValue = Dialogs.create().owner(stage).title("String Parameter")
					.masthead("Hack: add several divided by ','.\nPress 'Cancel' to finish enter.").message("Enter string value: ")
					.showTextInput("");
			if (stringValue.isPresent()) {
				values.add(stringValue.get());
			} else {
				break;
			}
		}
		String domen = "'";
		for (int i = 0; i < values.size(); ++i) {
			domen += values.get(i);
			if (i < values.size() - 1) {
				domen += "', '";
			}
		}
		domen += "'";
		textModel.add(new TextAlgorithmParameter(parameterName, SUB_EXECUTIONS_TYPE, domen));
	}

	private void connectCreateExecution() {
		createExecution.setOnAction(e -> {
			valid = true;
			stage.close();
		});
	}

	private boolean isValid() {
		return valid;
	}
}
