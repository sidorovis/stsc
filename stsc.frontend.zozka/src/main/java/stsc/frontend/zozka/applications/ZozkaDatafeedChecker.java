package stsc.frontend.zozka.applications;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.frontend.zozka.dialogs.TextFieldDialog;
import stsc.yahoo.liquiditator.StockFilter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ZozkaDatafeedChecker extends Application {

	private Stage owner;
	private final Parent gui;
	private static final StockFilter filter = new StockFilter();

	public final class StockLoadTask extends Task<Integer> {

		private final String datafeed;
		private boolean stopped = false;

		StockLoadTask(String datafeed) {
			this.datafeed = datafeed;
		}

		@Override
		protected Integer call() throws Exception {
			final File folder = new File(datafeed + "/data/");
			final File[] listOfFiles = folder.listFiles();
			final int size = listOfFiles.length;
			for (int id = 0; id < listOfFiles.length; ++id) {
				final File file = listOfFiles[id];
				final String filename = file.getName();
				if (file.isFile() && filename.endsWith(UnitedFormatStock.EXTENSION)) {
					loadStock(id, file);
					updateProgress((long) id, (long) size);
				}
				if (stopped)
					return id;
			}
			return size;
		}

		public void stopLoad() {
			stopped = true;
		}

	}

	static public final class StockDescription {
		private final IntegerProperty id;
		private final StringProperty name;
		private final BooleanProperty valid;
		private final BooleanProperty liquid;

		private final Stock stock;

		public StockDescription(int id, UnitedFormatStock stock) {
			this.id = new SimpleIntegerProperty(id);
			this.name = new SimpleStringProperty(stock.getName());
			this.valid = new SimpleBooleanProperty(true);
			this.liquid = new SimpleBooleanProperty(validateStock(stock));
			this.stock = stock;
		}

		public IntegerProperty idProperty() {
			return id;
		}

		public StringProperty nameProperty() {
			return name;
		}

		public BooleanProperty validProperty() {
			return valid;
		}

		public BooleanProperty liquidProperty() {
			return liquid;
		}

		public Stock getStock() {
			return stock;
		}
	}

	final private ObservableList<StockDescription> model = FXCollections.observableArrayList();

	@FXML
	private Label datafeedPath;
	private String datafeed;

	@FXML
	private TableView<StockDescription> stocksTable;
	@FXML
	private TableColumn<StockDescription, Number> stockIdColumn;
	@FXML
	private TableColumn<StockDescription, String> stockNameColumn;
	@FXML
	private TableColumn<StockDescription, Boolean> stockValidColumn;
	@FXML
	private TableColumn<StockDescription, Boolean> stockLiquidColumn;

	public ZozkaDatafeedChecker() throws IOException {
		final URL location = ZozkaDatafeedChecker.class.getResource("04_zozka_datafeed_checker.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		this.gui = loader.load();
	}

	@Override
	public void start(final Stage owner) throws Exception {
		this.owner = owner;
		Scene scene = new Scene(gui);
		owner.setScene(scene);
		owner.show();
		validateGui();
		setupTable();
		loadDatafeed();
		connectDoubleClickToTable();
	}

	private void connectDoubleClickToTable() {
		stocksTable.setOnMouseClicked(e -> {
			if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
				final StockDescription s = stocksTable.getSelectionModel().getSelectedItem();
				if (s != null) {
					showStockDescriptionDialog(s);
				}
			}
		});
	}

	private void showStockDescriptionDialog(StockDescription s) {
		final String errors = filter.test(s.stock);
		new TextFieldDialog(owner, "Stock validation errors: " + s.stock.getName(), errors).show();
	}

	private void setupTable() {
		stocksTable.setItems(model);
		stockIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
		stockNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		stockValidColumn.setCellValueFactory(cellData -> cellData.getValue().validProperty());
		stockValidColumn.setCellFactory(CheckBoxTableCell.forTableColumn(stockValidColumn));
		stockLiquidColumn.setCellValueFactory(cellData -> cellData.getValue().liquidProperty());
		stockLiquidColumn.setCellFactory(CheckBoxTableCell.forTableColumn(stockLiquidColumn));
	}

	private void validateGui() {
		assert stocksTable != null : "fx:id=\"stocksTable\" was not injected: check your FXML file.";
		assert stockIdColumn != null : "fx:id=\"stockIdColumn\" was not injected: check your FXML file.";
		assert stockNameColumn != null : "fx:id=\"stockNameColumn\" was not injected: check your FXML file.";
		assert stockValidColumn != null : "fx:id=\"stockValidationColumn\" was not injected: check your FXML file.";
		assert stockLiquidColumn != null : "fx:id=\"stockLiquidColumn\" was not injected: check your FXML file.";
	}

	@FXML
	public void datafeedEdit(final MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
			chooseFolder();
		}
	}

	private void chooseFolder() {
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
			try {
				loadDatafeed();
			} catch (IOException e) {
				Dialogs.create().showException(e);
			}
		}
	}

	private void loadDatafeed() throws IOException {
		if (datafeed == null || datafeed != datafeedPath.getText()) {
			datafeed = datafeedPath.getText();
			model.clear();

			final StockLoadTask task = new StockLoadTask(datafeed);
			final Thread loadThread = new Thread(task);
			loadThread.start();
			final Dialogs loadDialog = Dialogs.create().owner(owner).title("Stock Storage loading").message("Loading...");
			loadDialog.showWorkerProgress(task);
			task.setOnSucceeded(eh -> Platform.runLater(() -> {
				try {
					loadThread.join();
				} catch (Exception e) {
					Dialogs.create().showException(e);
				}
			}));
			task.setOnCancelled(eh -> {
				task.stopLoad();
			});
		}
	}

	private void loadStock(int id, File file) throws IOException {
		final UnitedFormatStock stock = UnitedFormatStock.readFromUniteFormatFile(file.getAbsolutePath());
		model.add(new StockDescription(id++, stock));
	}

	private static boolean validateStock(Stock stock) {
		return filter.test(stock) == null;
	}

	public static void main(String[] args) {
		Application.launch(ZozkaDatafeedChecker.class, args);
	}
}
