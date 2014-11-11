package stsc.frontend.zozka.panes;

import java.io.IOException;
import java.net.URL;
import java.util.Queue;

import org.controlsfx.dialog.Dialogs;

import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.components.ProgressBarTask;
import stsc.frontend.zozka.models.StockDescription;
import stsc.frontend.zozka.panes.internal.ProgressWithStopPane;
import stsc.yahoo.YahooFileStockStorage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StockDatafeedListPane {

	private final Stage owner;
	private final Parent gui;

	private StockStorage stockStorage;

	@FXML
	private Label label;
	@FXML
	private BorderPane borderPane;

	private ObservableList<StockDescription> model = FXCollections.observableArrayList();
	@FXML
	private TableView<StockDescription> table;
	@FXML
	private TableColumn<StockDescription, Integer> idColumn;
	@FXML
	private TableColumn<StockDescription, String> stockColumn;
	@FXML
	private TableColumn<StockDescription, Boolean> liquidColumn;
	@FXML
	private TableColumn<StockDescription, Boolean> validColumn;

	private ProgressWithStopPane progressWithStopPane = new ProgressWithStopPane();

	public StockDatafeedListPane(final Stage owner, final String title) throws IOException {
		this.owner = owner;
		final URL location = EquityPane.class.getResource("04_stock_datafeed_list_pane.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		this.gui = loader.load();

		initialize();
		label.setText(title);
	}

	private void initialize() {
		validateGui();
		table.setItems(model);
		borderPane.setBottom(progressWithStopPane);
	}

	private void validateGui() {
		assert label != null : "fx:id=\"label\" was not injected: check your FXML file.";
		assert borderPane != null : "fx:id=\"borderPane\" was not injected: check your FXML file.";
		assert table != null : "fx:id=\"table\" was not injected: check your FXML file.";
		assert idColumn != null : "fx:id=\"idColumn\" was not injected: check your FXML file.";
		assert stockColumn != null : "fx:id=\"stockColumn\" was not injected: check your FXML file.";
		assert liquidColumn != null : "fx:id=\"liquidColumn\" was not injected: check your FXML file.";
		assert validColumn != null : "fx:id=\"validColumn\" was not injected: check your FXML file.";
	}

	public Parent getGui() {
		return gui;
	}

	public void loadDatafeed(String string) {
		model.clear();
		try {
			progressWithStopPane.setVisible(true);

			final YahooFileStockStorage ss = new YahooFileStockStorage(string, string, false);
			final Queue<String> queue = ss.getTasks();
			final int allSize = queue.size();

			Thread t = new Thread(() -> {
				while (!queue.isEmpty()) {
					final int value = queue.size();
					progressWithStopPane.setIndicatorProgress((double) value / allSize);
				}
				progressWithStopPane.setIndicatorProgress(100.0);
				progressWithStopPane.setVisible(false);
			});
			t.start();
			stockStorage = ss;
			ss.startLoadStocks();
		} catch (ClassNotFoundException | IOException e) {
			Dialogs.create().owner(owner).showException(e);
		}
	}
}
