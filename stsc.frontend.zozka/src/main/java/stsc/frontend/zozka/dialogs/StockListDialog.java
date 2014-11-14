package stsc.frontend.zozka.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialog;

import stsc.frontend.zozka.models.StockDescription;

public class StockListDialog extends Dialog {

	private final ObservableList<StockDescription> model = FXCollections.observableArrayList();
	private final BorderPane borderPane = new BorderPane();
	private final TableView<StockDescription> table = new TableView<>();

	private final TableColumn<StockDescription, Number> idColumn = new TableColumn<>();
	private final TableColumn<StockDescription, String> nameColumn = new TableColumn<>();
	private final TableColumn<StockDescription, Boolean> validColumn = new TableColumn<>();

	public StockListDialog(Stage owner, String title) {
		super(owner, title);
		this.getWindow().setWidth(640);
		this.getWindow().setHeight(480);
		this.setContent(borderPane);
		borderPane.setCenter(table);
		borderPane.setBottom(new Label());
		configurateTable();
	}

	private void configurateTable() {
		table.setItems(model);
		idColumn.setText("Id");
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
		table.getColumns().add(idColumn);
		nameColumn.setText("Stock Name");
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		nameColumn.setPrefWidth(350.0);
		table.getColumns().add(nameColumn);
		validColumn.setText("Valid");
		validColumn.setCellValueFactory(cellData -> cellData.getValue().validProperty());
		validColumn.setPrefWidth(80);
		validColumn.setCellFactory(CheckBoxTableCell.forTableColumn(validColumn));
		table.getColumns().add(validColumn);
	}

	public ObservableList<StockDescription> getModel() {
		return model;
	}
}
