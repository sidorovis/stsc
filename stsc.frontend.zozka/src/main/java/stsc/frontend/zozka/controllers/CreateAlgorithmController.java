package stsc.frontend.zozka.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;

import org.controlsfx.dialog.Dialogs;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.frontend.zozka.gui.models.AlgorithmType;
import stsc.frontend.zozka.gui.models.ExecutionDescription;
import stsc.frontend.zozka.gui.models.NumberAlgorithmParameter;
import stsc.frontend.zozka.gui.models.ParameterType;
import stsc.frontend.zozka.gui.models.TextAlgorithmParameter;
import stsc.frontend.zozka.settings.ControllerHelper;
import stsc.storage.AlgorithmsStorage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

	private final Stage stage;
	private boolean valid;
	private ExecutionDescription executionDescriptionModel;

	public static final Pattern parameterNamePattern = Pattern.compile("^([\\w_\\d])+$");
	public static final Pattern integerParPattern = Pattern.compile("^-?(\\d)+$");
	public static final Pattern doubleParPattern = Pattern.compile("^-?(\\d)+(\\.(\\d)+)?$");

	@FXML
	private ComboBox<AlgorithmType> algorithmType;
	@FXML
	private ComboBox<String> algorithmClass;
	@FXML
	private Button questionButton;
	@FXML
	private TextField executionName;

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
	private Button saveExecution;

	public CreateAlgorithmController(final Stage owner) throws IOException {
		stage = new Stage();
		valid = false;
		final URL location = CreateAlgorithmController.class.getResource("01_create_algorithm.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		final Parent gui = loader.load();
		stage.initOwner(owner);
		stage.initModality(Modality.WINDOW_MODAL);
		final Scene scene = new Scene(gui);
		stage.setScene(scene);
		stage.setMinHeight(480);
		stage.setMinWidth(640);
		stage.setTitle("Create Algorithm Settings");
		stage.centerOnScreen();
	}

	public CreateAlgorithmController(final Stage owner, final ExecutionDescription executionDescription) throws IOException {
		this(owner);
		setExecutionDescription(executionDescription);
	}

	public ExecutionDescription getExecutionDescription() {
		this.stage.showAndWait();
		if (isValid()) {
			return executionDescriptionModel;
		}
		return null;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		validateGui();
		executionDescriptionModel = new ExecutionDescription(algorithmType.getValue(), executionName.getText(), algorithmClass.getValue());
		connectActionsForAlgorithmType();
		connectActionsForAlgorithmClass();
		connectQuestionButton();
		connectTableForNumber();
		connectTableForText();
		connectAddParameter();
		connectSaveExecution();
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
		assert saveExecution != null : "fx:id=\"saveExecution\" was not injected: check your FXML file.";
		valid = false;
	}

	private void connectActionsForAlgorithmType() {
		algorithmType.setItems(AlgorithmType.getObservableList());
		algorithmType.getSelectionModel().select(0);
		algorithmType.valueProperty().addListener(new ChangeListener<AlgorithmType>() {
			@Override
			public void changed(ObservableValue<? extends AlgorithmType> observable, AlgorithmType oldValue, AlgorithmType newValue) {
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

	protected void populateAlgorithmClassWith(AlgorithmType newValue) throws BadAlgorithmException {
		final ObservableList<String> model = algorithmClass.getItems();
		model.clear();
		if (newValue.isStock()) {
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
		ControllerHelper.connectDeleteAction(stage, numberTable, executionDescriptionModel.getNumberAlgorithms());

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
		ControllerHelper.connectDeleteAction(stage, textTable, executionDescriptionModel.getTextAlgorithms());

		textParName.setCellValueFactory(new PropertyValueFactory<TextAlgorithmParameter, String>("parameterName"));
		textParName.setCellFactory(TextFieldTableCell.forTableColumn());
		textParType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));

		connectTextColumn(textParDomen, "domen");
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
			final Optional<ParameterType> parameterType = getParameterType();
			if (!parameterType.isPresent()) {
				return;
			}
			switch (parameterType.get()) {
			case DOUBLE:
				addDoubleParameter(parameterName.get(), "0.0", "1.0", "22.0");
				break;
			case INTEGER:
				addIntegerParameter(parameterName.get(), "0", "1", "22");
				break;
			case STRING:
				addStringParameter(parameterName.get());
				break;
			case SUB_EXECUTION:
				addSubExecutionParameter(parameterName.get());
				break;
			default:
				break;
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
		for (NumberAlgorithmParameter p : executionDescriptionModel.getNumberAlgorithms()) {
			if (p.parameterNameProperty().get().equals(parameterName)) {
				return true;
			}
		}
		for (TextAlgorithmParameter p : executionDescriptionModel.getTextAlgorithms()) {
			if (p.parameterNameProperty().get().equals(parameterName)) {
				return true;
			}
		}
		return false;
	}

	private Optional<ParameterType> getParameterType() {
		return Dialogs.create().owner(stage).title("Choose type for parameter").masthead(null).message("Type define parameter domen: ")
				.showChoices(ParameterType.values());
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
		executionDescriptionModel.getNumberAlgorithms().add(
				new NumberAlgorithmParameter(parameterName, ParameterType.INTEGER, integerParPattern, from, step, to));
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
		executionDescriptionModel.getNumberAlgorithms().add(
				new NumberAlgorithmParameter(parameterName, ParameterType.DOUBLE, doubleParPattern, from, step, to));
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
		final List<String> values = getStringDomen("String Parameter");
		final String domen = TextAlgorithmParameter.createStringRepresentation(values);
		executionDescriptionModel.getTextAlgorithms().add(new TextAlgorithmParameter(parameterName, ParameterType.STRING, domen));
	}

	private void addSubExecutionParameter(String parameterName) {
		final List<String> values = getStringDomen("SubExecution Parameter");
		final String domen = TextAlgorithmParameter.createStringRepresentation(values);
		executionDescriptionModel.getTextAlgorithms().add(new TextAlgorithmParameter(parameterName, ParameterType.SUB_EXECUTION, domen));
	}

	private List<String> getStringDomen(String title) {
		final ArrayList<String> values = new ArrayList<>();
		while (true) {
			final Optional<String> stringValue = Dialogs.create().owner(stage).title(title)
					.masthead("Hack: add several divided by ','.\nPress 'Cancel' to finish enter.").message("Enter domen value: ")
					.showTextInput("");
			if (stringValue.isPresent()) {
				values.add(stringValue.get());
			} else {
				break;
			}
		}
		return values;
	}

	private void connectSaveExecution() {
		saveExecution.setOnAction(e -> {
			valid = true;
			stage.close();
		});
	}

	private boolean isValid() {
		executionDescriptionModel.setAlgorithmName(this.algorithmClass.getValue());
		executionDescriptionModel.setExecutionName(this.executionName.getText());
		executionDescriptionModel.setAlgorithmType(this.algorithmType.getValue());
		return valid;
	}

	private void setExecutionDescription(ExecutionDescription ed) {
		executionDescriptionModel = ed;
	}
}
