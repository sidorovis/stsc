package stsc.frontend.zozka.components;

import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.stage.Window;

import org.controlsfx.dialog.Dialogs;

import stsc.common.storage.StockStorage;
import stsc.yahoo.YahooFileStockStorage;

public class DatafeedLoader {

	private final Window owner;
	private Thread loadThread;
	private final YahooFileStockStorage yfStockStorage;

	public DatafeedLoader(final Window owner, final File datafeed) throws Exception {
		if (!(new File(datafeed + "/data").isDirectory()) || !(new File(datafeed + "/filtered_data").isDirectory())) {
			throw new Exception("Datafeed folder should contain ./data and ./filtered_data folders.");
		}
		this.owner = owner;
		final String path = datafeed.getAbsolutePath();
		this.yfStockStorage = new YahooFileStockStorage(path + "/data", path + "/filtered_data", false);
	}

	public void startLoad(EventHandler<WorkerStateEvent> successHandler, EventHandler<WorkerStateEvent> exitHandler)
			throws ClassNotFoundException, IOException, InterruptedException {
		final ProgressBarTask task = new ProgressBarTask(yfStockStorage);
		this.loadThread = new Thread(task);
		this.loadThread.start();
		Dialogs.create().owner(owner).title("Stock Storage loading").message("Loading...").showWorkerProgress(task);
		task.setOnSucceeded(eh -> Platform.runLater(() -> {
			try {
				yfStockStorage.waitForLoad();
			} catch (Exception e) {
				Dialogs.create().showException(e);
			}
			successHandler.handle(eh);
		}));
		task.setOnFailed(exitHandler);
		task.setOnCancelled(exitHandler);
		yfStockStorage.startLoadStocks();
	}

	public StockStorage getStockStorage() throws InterruptedException {
		return yfStockStorage;
	}

}
