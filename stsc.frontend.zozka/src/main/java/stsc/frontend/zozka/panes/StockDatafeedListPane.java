package stsc.frontend.zozka.panes;

import java.io.IOException;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import org.controlsfx.dialog.Dialogs;

import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.models.StockDescription;
import stsc.frontend.zozka.panes.internal.ProgressWithStopPane;
import stsc.yahoo.YahooFileStockStorage;
import stsc.yahoo.liquiditator.StockFilter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StockDatafeedListPane extends BorderPane {

	private static final StockFilter stockFilter = new StockFilter();

	private final Stage owner;

	private StockStorage stockStorage;

	@FXML
	private Label label;

	private ObservableList<StockDescription> model = FXCollections.observableArrayList();
	@FXML
	private TableView<StockDescription> table;
	@FXML
	private TableColumn<StockDescription, Number> idColumn;
	@FXML
	private TableColumn<StockDescription, String> stockColumn;
	@FXML
	private TableColumn<StockDescription, Boolean> liquidColumn;
	@FXML
	private TableColumn<StockDescription, Boolean> validColumn;

	private ProgressWithStopPane progressWithStopPane = new ProgressWithStopPane();

	public StockDatafeedListPane(final Stage owner, final String title) throws IOException {
		this.owner = owner;
		final URL location = StockDatafeedListPane.class.getResource("04_stock_datafeed_list_pane.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		final Parent gui = loader.load();
		setCenter(gui);

		initialize();
		label.setText(title);
	}

	private void initialize() {
		validateGui();
		table.setItems(model);
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
		stockColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		liquidColumn.setCellValueFactory(cellData -> cellData.getValue().liquidProperty());
		liquidColumn.setCellFactory(CheckBoxTableCell.forTableColumn(liquidColumn));
		validColumn.setCellValueFactory(cellData -> cellData.getValue().validProperty());
		validColumn.setCellFactory(CheckBoxTableCell.forTableColumn(validColumn));
		setBottom(null);
	}

	private void validateGui() {
		assert label != null : "fx:id=\"label\" was not injected: check your FXML file.";
		assert table != null : "fx:id=\"table\" was not injected: check your FXML file.";
		assert idColumn != null : "fx:id=\"idColumn\" was not injected: check your FXML file.";
		assert stockColumn != null : "fx:id=\"stockColumn\" was not injected: check your FXML file.";
		assert liquidColumn != null : "fx:id=\"liquidColumn\" was not injected: check your FXML file.";
		assert validColumn != null : "fx:id=\"validColumn\" was not injected: check your FXML file.";
	}

	public void loadDatafeed(final String datafeedPath) {
		loadDatafeed(datafeedPath, null);
	}

	public void loadDatafeed(final String datafeedPath, Runnable onFinish) {
		model.clear();
		try {
			setBottom(progressWithStopPane);
			final YahooFileStockStorage ss = new YahooFileStockStorage(datafeedPath, datafeedPath, false);
			setStockStorage(ss);
			setUpdateModel(ss);
			startLoadIndicatorUpdates(ss, onFinish);
			ss.startLoadStocks();
			setProgressStopButton(ss);
		} catch (ClassNotFoundException | IOException e) {
			Dialogs.create().owner(owner).showException(e);
		}
	}

	private void setUpdateModel(final YahooFileStockStorage ss) {
		final AtomicInteger index = new AtomicInteger(0);
		ss.addReceiver(newStock -> Platform.runLater(() -> {
			final boolean liquid = stockFilter.test(newStock) == null;
			synchronized (model) {
				model.add(new StockDescription(index.getAndIncrement(), newStock, liquid));
			}
		}));
	}

	private void startLoadIndicatorUpdates(final YahooFileStockStorage ss, Runnable onFinish) {
		final Queue<String> queue = ss.getTasks();
		final Thread t = new Thread(() -> {
			try {
				updateIndicatorValue(queue);
				Platform.runLater(() -> {
					progressWithStopPane.setIndicatorProgress(100.0);
					setBottom(null);
					if (onFinish != null) {
						onFinish.run();
					}
				});
			} catch (Exception e) {
				Dialogs.create().owner(owner).showException(e);
			}
		});
		t.start();
	}

	private void updateIndicatorValue(final Queue<String> queue) throws InterruptedException {
		final int allSize = queue.size();
		while (!queue.isEmpty()) {
			final int value = allSize - queue.size();
			Platform.runLater(() -> {
				progressWithStopPane.setIndicatorProgress((double) value / allSize);
			});
			Thread.sleep(300);
		}
	}

	private void setProgressStopButton(final YahooFileStockStorage ss) {
		progressWithStopPane.setOnStopButtonAction(() -> {
			try {
				ss.stopLoadStocks();
				ss.waitForLoad();
			} catch (Exception e) {
				Dialogs.create().owner(owner).showException(e);
			}
		});
	}

	public StockStorage getStockStorage() {
		return stockStorage;
	}

	private void setStockStorage(StockStorage stockStorage) {
		this.stockStorage = stockStorage;
	}
}
