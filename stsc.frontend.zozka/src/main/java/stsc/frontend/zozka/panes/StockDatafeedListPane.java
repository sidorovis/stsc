package stsc.frontend.zozka.panes;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.controlsfx.dialog.Dialogs;

import stsc.common.stocks.Stock;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.models.StockDescription;
import stsc.frontend.zozka.panes.internal.ProgressWithStopPane;
import stsc.yahoo.YahooFileStockStorage;
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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StockDatafeedListPane extends BorderPane {

	private final Stage owner;

	private YahooFileStockStorage stockStorage;

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

	public Set<String> loadDatafeed(final String datafeedPath, Function<Set<String>, Void> onFinish, Predicate<String> filter) {
		model.clear();
		final Set<String> result = new HashSet<>();
		try {
			setBottom(progressWithStopPane);
			final YahooFileStockStorage ss = new YahooFileStockStorage(datafeedPath, datafeedPath, false);
			final Queue<String> tasks = ss.getTasks();
			applySizeFilter(tasks, filter);
			result.addAll(tasks);
			postLoadDatafeedActions(onFinish, ss);
		} catch (ClassNotFoundException | IOException e) {
			Dialogs.create().owner(owner).showException(e);
		}
		return result;
	}

	private void postLoadDatafeedActions(Function<Set<String>, Void> onFinish, final YahooFileStockStorage ss)
			throws ClassNotFoundException, IOException {
		setStockStorage(ss);
		setUpdateModel(ss);
		startLoadIndicatorUpdates(ss, onFinish);
		ss.startLoadStocks();
		setProgressStopButton(ss);
	}

	private void applySizeFilter(Queue<String> tasks, Predicate<String> filter) {
		if (filter != null) {
			tasks.removeIf(filter);
		}
	}

	private void setUpdateModel(final YahooFileStockStorage ss) {
		final AtomicInteger index = new AtomicInteger(0);
		ss.addReceiver(newStock -> Platform.runLater(() -> {
			synchronized (model) {
				model.add(new StockDescription(index.getAndIncrement(), newStock));
			}
		}));
	}

	private void startLoadIndicatorUpdates(final YahooFileStockStorage ss, Function<Set<String>, Void> onFinish) {
		final Queue<String> queue = ss.getTasks();
		final Thread t = new Thread(() -> {
			try {
				updateIndicatorValue(queue);
				Platform.runLater(() -> {
					progressWithStopPane.setIndicatorProgress(100.0);
					setBottom(null);
					if (onFinish != null) {
						onFinish.apply(null);
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

	public void setOnMouseDoubleClick(final Function<StockDescription, Optional<Void>> function) {
		table.setOnMouseClicked(eh -> {
			if (eh.getButton() == MouseButton.PRIMARY && eh.getClickCount() == 2) {
				final StockDescription sd = table.getSelectionModel().getSelectedItem();
				if (sd != null) {
					function.apply(sd);
				}
			}
		});
	}

	public void updateStock(Stock newStockData) {
		stockStorage.updateStock(newStockData);
		updateModel(newStockData, model);
		table.setItems(model);
	}

	public static void updateModel(Stock newStockData, ObservableList<StockDescription> model) {
		model.forEach(new Consumer<StockDescription>() {
			@Override
			public void accept(StockDescription sd) {
				if (sd.getStock().getName().equals(newStockData.getName())) {
					sd.setStock(newStockData);
				}
			}
		});
	}

	public StockStorage getStockStorage() {
		return stockStorage;
	}

	private void setStockStorage(YahooFileStockStorage stockStorage) {
		this.stockStorage = stockStorage;
	}

}
