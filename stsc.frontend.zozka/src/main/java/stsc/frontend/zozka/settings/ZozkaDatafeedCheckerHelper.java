package stsc.frontend.zozka.settings;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.models.StockDescription;
import stsc.frontend.zozka.panes.StockDatafeedListPane;
import stsc.frontend.zozka.panes.StockViewPane;
import stsc.yahoo.YahooFileStockStorage;
import stsc.yahoo.downloader.YahooDownloadHelper;
import stsc.yahoo.liquiditator.InvalidDatafeedException;
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

	public void showAppropriateDialogForStockFromDataAndFiltered(Stage owner, final Stock data, final Stock filtered) {
		try {
			checkSizeOfDataSmallerThanFiltered(data, filtered);
			if (isLiquid(data) && isValid(data)) {
				showStockRepresentation(owner, data, filtered);
			} else {
				final boolean showRepresentation = checkLiquidityAndValidityAndRedownload(owner, data);
				if (showRepresentation) {
					showStockRepresentation(owner, data, filtered);
				}
			}
		} catch (Exception e) {
			Dialogs.create().owner(owner).showException(e);
		}
	}

	private void checkSizeOfDataSmallerThanFiltered(Stock data, Stock filtered) throws InvalidDatafeedException {
		if (data.getDays().size() < filtered.getDays().size())
			throw new InvalidDatafeedException("Stock data at common data have smaller size than filtered stock data.");
	}

	private void showStockRepresentation(Stage owner, Stock data, Stock filtered) {
		try {
			final Dialog dialog = new Dialog(owner, "ForAdjectiveClose");
			SplitPane splitPane = new SplitPane();
			splitPane.setOrientation(Orientation.VERTICAL);

			if (data != null) {
				final StockViewPane dataStockViewPane = StockViewPane.createPaneForAdjectiveClose(owner, data);
				splitPane.getItems().add(dataStockViewPane.getMainPane());
			}
			if (filtered != null) {
				final StockViewPane filteredDataStockViewPane = StockViewPane.createPaneForAdjectiveClose(owner, filtered);
				splitPane.getItems().add(filteredDataStockViewPane.getMainPane());
			}
			dialog.setContent(splitPane);
			dialog.show();
		} catch (IOException e) {
			Dialogs.create().owner(owner).showException(e);
		}
	}

	public void checkStockAndAskForUser(StockDescription sd, Stock data, Stock filtered, Stage owner) {
		if (isLiquid(sd.getStock()) && isValid(sd.getStock())) {
			showStockRepresentation(owner, data, filtered);
		} else {
			if (checkLiquidityAndValidityAndRedownload(owner, sd.getStock())) {
				showStockRepresentation(owner, data, filtered);
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
			final UnitedFormatStock s = YahooDownloadHelper.download(stockName);
			final String liquid = stockFilter.isLiquidTestWithError(s);
			final String valid = stockFilter.isValidWithError(s);
			String error = "";
			error += (liquid != null) ? liquid : "";
			error += (valid != null) ? valid : "";
			if (error.isEmpty()) {
				error = "Liquid and Valid test passed";
			}
			if (isUserAgreeForAction(owner, s, "Want you to save just downloaded stock?", error, "")) {
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
				final Stock dataStock = dataStockStorage.getStock(stockName);
				final Stock filteredDataStock = filteredDataStockStorage.getStock(stockName);
				if (dataStock.getDays().size() != filteredDataStock.getDays().size()) {
					notEqualStockList.add(stockName);
				} else if (ZozkaDatafeedCheckerHelper.isLiquid(dataStock) != ZozkaDatafeedCheckerHelper.isLiquid(filteredDataStock)) {
					notEqualStockList.add(stockName);
				} else if (ZozkaDatafeedCheckerHelper.isValid(dataStock) != ZozkaDatafeedCheckerHelper.isValid(filteredDataStock)) {
					notEqualStockList.add(stockName);
				}
			}
		}
		return notEqualStockList;
	}

}
