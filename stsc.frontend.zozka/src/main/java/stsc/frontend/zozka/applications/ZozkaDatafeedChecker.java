package stsc.frontend.zozka.applications;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.panes.StockDatafeedListPane;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ZozkaDatafeedChecker extends Application {

	private Stage owner;

	@FXML
	private BorderPane borderPane;
	@FXML
	private final Label datafeedPathLabel = new Label();
	private String datafeedPath;

	private StockDatafeedListPane dataStockList;
	private StockDatafeedListPane filteredStockDataList;

	public ZozkaDatafeedChecker() {
		datafeedPathLabel.setText("./test_data/");
	}

	@Override
	public void start(final Stage owner) throws Exception {
		this.owner = owner;
		owner.setScene(initializeGui());
		owner.setMinHeight(500);
		owner.setMinWidth(830);
		owner.setWidth(830);
		owner.show();
		connectDatafeedChange();
	}

	private void connectDatafeedChange() {
		datafeedPathLabel.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
				chooseFolder();
			}
		});
	}

	private Scene initializeGui() throws IOException {
		borderPane = new BorderPane();
		borderPane.setTop(datafeedPathLabel);
		final Scene scene = new Scene(borderPane);
		final SplitPane splitPane = new SplitPane();
		splitPane.setOrientation(Orientation.HORIZONTAL);
		dataStockList = new StockDatafeedListPane(owner, "Data");
		filteredStockDataList = new StockDatafeedListPane(owner, "Filtered data");
		addData(splitPane, dataStockList);
		addData(splitPane, filteredStockDataList);
		borderPane.setCenter(splitPane);
		return scene;
	}

	private void addData(SplitPane splitPane, StockDatafeedListPane listPane) {
		splitPane.getItems().add(listPane);
	}

	public void datafeedEdit(final MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
			chooseFolder();
		}
	}

	private void chooseFolder() {
		final String path = datafeedPathLabel.getText();
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
			datafeedPathLabel.setText(result.getAbsolutePath());
			try {
				loadDatafeed();
			} catch (IOException e) {
				Dialogs.create().showException(e);
			}
		}
	}

	private void loadDatafeed() throws IOException {
		if (datafeedPath == null || datafeedPath != datafeedPathLabel.getText()) {
			datafeedPath = datafeedPathLabel.getText();
			dataStockList.loadDatafeed(datafeedPath + "/data", () -> {
				filteredStockDataList.loadDatafeed(datafeedPath + "/filtered_data", () -> {
					checkLists();
				});
			});
		}
	}

	private void checkLists() {
		checkThatStocksAreEqual();
	}

	private void checkThatStocksAreEqual() {
		final StockStorage dataStockStorage = dataStockList.getStockStorage();
		final StockStorage filteredDataStockStorage = filteredStockDataList.getStockStorage();

		final Set<String> allList = dataStockStorage.getStockNames();
		final Set<String> filteredList = dataStockStorage.getStockNames();
	}

	public static void main(String[] args) {
		Application.launch(ZozkaDatafeedChecker.class, args);
	}
}
