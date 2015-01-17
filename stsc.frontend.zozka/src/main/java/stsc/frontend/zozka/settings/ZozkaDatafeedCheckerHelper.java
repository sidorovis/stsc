package stsc.frontend.zozka.settings;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.models.StockDescription;
import stsc.frontend.zozka.panes.StockDatafeedListPane;
import stsc.frontend.zozka.panes.CurvesViewPane;
import stsc.yahoo.YahooFileStockStorage;
import stsc.yahoo.downloader.YahooDownloadHelper;
import stsc.yahoo.liquiditator.StockFilter;

public class ZozkaDatafeedCheckerHelper {

	private static final StockFilter stockFilter = new StockFilter();

	private final String datafeedPath;
	private final StockDatafeedListPane dataStockList;
	private final StockDatafeedListPane filteredStockDataList;

	private ObservableList<StockDescription> dialogModel;

	public ZozkaDatafeedCheckerHelper(String datafeedPath, StockDatafeedListPane dataStockList,
			StockDatafeedListPane filteredStockDataList, ObservableList<StockDescription> dialogModel) {
		this.datafeedPath = datafeedPath;
		this.dataStockList = dataStockList;
		this.filteredStockDataList = filteredStockDataList;
		this.dialogModel = dialogModel;
	}

	public static boolean isLiquid(Stock s) {
		return stockFilter.isLiquid(s);
	}

	public static boolean isValid(Stock s) {
		return stockFilter.isValid(s);
	}

	private boolean showStockRepresentation(Stage owner, Stock data, Stock filtered, boolean askForSave) {
		try {
			final String stockRepresentationTitle = generateRepresentationTitle(askForSave);
			final Dialog dialog = new Dialog(owner, stockRepresentationTitle);
			final BorderPane borderPane = new BorderPane();
			SplitPane splitPane = new SplitPane();
			splitPane.setOrientation(Orientation.VERTICAL);

			if (data != null) {
				final CurvesViewPane dataStockViewPane = CurvesViewPane.createPaneForAdjectiveClose(owner, data);
				splitPane.getItems().add(dataStockViewPane.getMainPane());
			}
			if (filtered != null) {
				final CurvesViewPane filteredDataStockViewPane = CurvesViewPane.createPaneForAdjectiveClose(owner, filtered);
				splitPane.getItems().add(filteredDataStockViewPane.getMainPane());
			}
			borderPane.setCenter(splitPane);
			dialog.setContent(borderPane);
			if (askForSave) {
				final String error = createErrorMessage(data);
				Dialogs.create().owner(owner).masthead(null).message(error).showInformation();
				final HBox hbox = new HBox();
				final Button saveButton = new Button("Save");
				final Button exitButton = new Button("Exit");
				hbox.setAlignment(Pos.CENTER);
				saveButton.setDefaultButton(true);
				hbox.getChildren().add(saveButton);
				hbox.getChildren().add(exitButton);
				borderPane.setBottom(hbox);
				saveButton.setOnAction(e -> {
					dialog.setResult(Dialog.Actions.YES);
					dialog.hide();
				});
				exitButton.setOnAction(e -> dialog.hide());
			}
			dialog.getWindow().setHeight(700);
			final Action a = dialog.show();
			return a == Dialog.Actions.YES;
		} catch (IOException e) {
			Dialogs.create().owner(owner).showException(e);
		}
		return false;
	}

	private String createErrorMessage(Stock s) {
		final String liquid = stockFilter.isLiquidTestWithError(s);
		final String valid = stockFilter.isValidWithError(s);
		String error = "";
		error += (liquid != null) ? liquid : "";
		error += (valid != null) ? valid : "";
		if (error.isEmpty()) {
			error = "Liquid and Valid test passed";
		}
		return error;
	}

	private String generateRepresentationTitle(boolean askForSave) {
		if (askForSave) {
			return "ForAdjectiveClose - do you want to save it?";
		} else {
			return "ForAdjectiveClose";
		}
	}

	public void checkStockAndAskForUser(Stock toTest, Stock data, Stock filtered, Stage owner) {
		if (isLiquid(toTest) && isValid(toTest)) {
			showStockRepresentation(owner, data, filtered, false);
		} else {
			if (checkLiquidityAndValidityAndRedownload(owner, toTest)) {
				showStockRepresentation(owner, data, filtered, false);
			}
		}
	}

	/**
	 * @return true it download was cancelled
	 */
	private boolean checkLiquidityAndValidityAndRedownload(Stage owner, Stock stock) {
		boolean downloadCancelled = checkLiquidutyAndRedownload(owner, stock);
		if (downloadCancelled) {
			return true;
		}
		downloadCancelled = checkValidityAndRedownload(owner, stock);
		if (downloadCancelled) {
			return true;
		}
		return false;
	}

	private boolean checkLiquidutyAndRedownload(Stage owner, Stock stock) {
		if (!isLiquid(stock)) {
			return askUserForRedownloadAndRedownload(owner, stock, stockFilter.isLiquidTestWithError(stock), " not liquid");
		}
		return false;
	}

	private boolean checkValidityAndRedownload(Stage owner, Stock stock) {
		if (!isValid(stock)) {
			return askUserForRedownloadAndRedownload(owner, stock, stockFilter.isValidWithError(stock), " not valid");
		}
		return false;
	}

	private boolean askUserForRedownloadAndRedownload(Stage owner, Stock stock, String error, String value) {
		if (isUserAgreeForAction(owner, stock, "Want you redownload data?", error, value)) {
			return redownloadStock(owner, stock.getName());
		} else {
			return true;
		}
	}

	private boolean isUserAgreeForAction(Stage owner, Stock stock, String title, String errorString, String mastheadPostfix) {
		final Action response = Dialogs.create().owner(owner).title(title).masthead("Stock " + stock.getName() + mastheadPostfix)
				.message(errorString).showConfirm();
		return response == Dialog.Actions.YES;
	}

	/**
	 * @param owner
	 * @return true if user refuse re-download or exception appear
	 */
	private boolean redownloadStock(Stage owner, String stockName) {
		try {
			final Optional<UnitedFormatStock> sPtr = YahooDownloadHelper.download(stockName);
			final Optional<Stock> stockPtr = dataStockList.getStockStorage().getStock(stockName);
			if (!sPtr.isPresent() || !stockPtr.isPresent()) {
				return false;
			}
			final UnitedFormatStock s = sPtr.get();
			final boolean isSave = showStockRepresentation(owner, s, stockPtr.get(), true);
			if (isSave) {
				s.storeUniteFormatToFolder(datafeedPath + YahooFileStockStorage.DATA_FOLDER);
				dataStockList.updateStock(s);
				if (isLiquid(s) && isValid(s) || filteredStockDataList.getStockStorage().getStock(s.getName()) != null) {
					s.storeUniteFormatToFolder(datafeedPath + YahooFileStockStorage.FILTER_DATA_FOLDER);
					filteredStockDataList.updateStock(s);
				} else {
					YahooDownloadHelper.deleteFilteredFile(true, datafeedPath + YahooFileStockStorage.FILTER_DATA_FOLDER, stockName);
				}
				updateDialogModel(s);
				return false;
			} else {
				return true;
			}
		} catch (InterruptedException | IOException e) {
			Dialogs.create().owner(owner).showException(e);
		}
		return true;
	}

	private void updateDialogModel(UnitedFormatStock s) {
		if (dialogModel != null) {
			StockDatafeedListPane.updateModel(s, dialogModel);
		}
	}

	public static Set<String> findDifferenceByDaysSizeAndStockFilter(final StockStorage dataStockStorage,
			final StockStorage filteredDataStockStorage, final Set<String> allList, final Set<String> filteredList) {
		final Set<String> notEqualStockList = new HashSet<>();
		for (String stockName : allList) {
			if (filteredList.contains(stockName)) {
				final Optional<Stock> dataStockPtr = dataStockStorage.getStock(stockName);
				final Optional<Stock> filteredDataStockPtr = filteredDataStockStorage.getStock(stockName);
				if (!dataStockPtr.isPresent() || !filteredDataStockPtr.isPresent()) {
					return notEqualStockList;
				}
				if (dataStockPtr.get().getDays().size() != filteredDataStockPtr.get().getDays().size()) {
					notEqualStockList.add(stockName);
				} else if (ZozkaDatafeedCheckerHelper.isLiquid(dataStockPtr.get()) != ZozkaDatafeedCheckerHelper
						.isLiquid(filteredDataStockPtr.get())) {
					notEqualStockList.add(stockName);
				} else if (ZozkaDatafeedCheckerHelper.isValid(dataStockPtr.get()) != ZozkaDatafeedCheckerHelper
						.isValid(filteredDataStockPtr.get())) {
					notEqualStockList.add(stockName);
				}
			}
		}
		return notEqualStockList;
	}

}
