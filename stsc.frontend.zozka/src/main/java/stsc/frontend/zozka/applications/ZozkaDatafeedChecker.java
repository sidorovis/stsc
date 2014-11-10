package stsc.frontend.zozka.applications;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.controlsfx.dialog.Dialogs;

import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.components.DatafeedLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ZozkaDatafeedChecker extends Application {

	private Stage owner;
	private final Parent gui;

	static public final class StockDescription {
		private final StringProperty name;
		private final BooleanProperty valid;
		private final BooleanProperty liquid;

		public StockDescription(String name, Boolean valid, Boolean liquid) {
			this.name = new SimpleStringProperty(name);
			this.valid = new SimpleBooleanProperty(valid);
			this.liquid = new SimpleBooleanProperty(liquid);
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
	}

	final private ObservableList<StockDescription> model = FXCollections.observableArrayList();

	@FXML
	private Label datafeedPath;
	private String datafeed;
	private StockStorage stockStorage;

	@FXML
	private TableView<StockDescription> stocksTable;
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
	}

	private void setupTable() {
		stocksTable.setItems(model);

	}

	private void validateGui() {
		assert stocksTable != null : "fx:id=\"stocksTable\" was not injected: check your FXML file.";
		assert stockNameColumn != null : "fx:id=\"stockNameColumn\" was not injected: check your FXML file.";
		assert stockValidColumn != null : "fx:id=\"stockValidationColumn\" was not injected: check your FXML file.";
		assert stockLiquidColumn != null : "fx:id=\"stockLiquidColumn\" was not injected: check your FXML file.";
	}

	@FXML
	public void datafeedEdit(final MouseEvent mouseEvent) {
		System.out.println(mouseEvent);
	}

	private void loadDatafeed() {
		if (datafeed == null || datafeed != datafeedPath.getText()) {
			datafeed = datafeedPath.getText();
			try {
				final DatafeedLoader loader = new DatafeedLoader(owner, new File(datafeed));
				loader.startLoad(sh -> {
					try {
						stockStorage = loader.getStockStorage();
						Platform.runLater(() -> {
							reloadModel();
						});
					} catch (Exception e) {
						Dialogs.create().showException(e);
					}
				}, eh -> {
					stockStorage = null;
					Dialogs.create().title("Datafeed load failed").masthead(null).message("Error: " + eh.toString()).showError();
				});
			} catch (Exception e) {
				Dialogs.create().showException(e);
			}
		}
	}

	private synchronized void reloadModel() {
		model.clear();
		for (String stockName : stockStorage.getStockNames()) {
			model.add(new StockDescription(stockName, true, true));
		}
	}

	public static void main(String[] args) {
		Application.launch(ZozkaDatafeedChecker.class, args);
	}
}
