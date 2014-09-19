package stsc.frontend.zozka.settings;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridFactory;
import stsc.yahoo.YahooFileStockStorage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.DatePicker;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class SimulatorSettingsBuilder extends Application {

	private static AtomicLong factoryId = new AtomicLong();

	private static class FactoryStorage {
		private CountDownLatch countDownLatch = new CountDownLatch(1);
		private SimulatorSettingsGridFactory factory;

		public void setFactory(SimulatorSettingsGridFactory factory) {
			this.factory = factory;
			countDownLatch.countDown();
		}
	}

	private static Map<Long, FactoryStorage> answers = Collections.synchronizedMap(new HashMap<>());

	public static SimulatorSettingsGridFactory run(String initialStockStorage, String[] initialArgs) throws InterruptedException {
		final long id = factoryId.incrementAndGet();
		final FactoryStorage factoryStorage = new FactoryStorage();
		answers.put(id, factoryStorage);
		final String[] args = new String[initialArgs.length + 2];
		for (int i = 0; i < initialArgs.length; ++i) {
			args[i] = initialArgs[i];
		}
		args[initialArgs.length] = "--id=" + id;
		args[initialArgs.length + 1] = "--stockStorage=" + initialStockStorage;
		Application.launch(args);
		factoryStorage.countDownLatch.await();
		return factoryStorage.factory;
	}

	@Override
	public void start(Stage stage) throws Exception {
		final long id = Long.valueOf(this.getParameters().getNamed().get("id"));
		final String stockStoragePath = getStockStoragePath(stage);
		final StockStorage stockStorage = loadStockStorage(stockStoragePath);
		final Date from = getDate(stage);
		final Date to = getDate(stage);
		answers.get(id).setFactory(new SimulatorSettingsGridFactory(stockStorage, new FromToPeriod(from, to)));
		Platform.exit();
	}

	private String getStockStoragePath(Stage stage) throws InvalidAlgorithmParameterException {
		final String initialStockStorage = this.getParameters().getNamed().get("stockStorage");
		if (initialStockStorage == null || initialStockStorage.equals("null")) {
			return getPath(stage);
		}
		return initialStockStorage;
	}

	private String getPath(Stage stage) throws InvalidAlgorithmParameterException {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("");
		File f = dc.showDialog(stage);
		if (f.isDirectory()) {
			return f.getAbsolutePath();
		}
		throw new InvalidAlgorithmParameterException("Choosed path is incorrect");
	}

	private StockStorage loadStockStorage(String stockStoragePath) throws ClassNotFoundException, IOException, InterruptedException {
		final String dataSubFolder = getOrSetDefault("dataSubFolder", "./data");
		final String filteredDataSubFolder = getOrSetDefault("filteredDataSubFolder", "./filtered_data");
		return new YahooFileStockStorage(stockStoragePath + dataSubFolder, stockStoragePath + filteredDataSubFolder);
	}

	private String getOrSetDefault(String key, String defaultValue) {
		final String value = this.getParameters().getNamed().get(key);
		return (value == null ? defaultValue : value);
	}

	private Date getDate(Stage stage) {
		final DatePicker dp = new DatePicker();
		dp.show();
		final Instant i = dp.getValue().atStartOfDay().atZone(ZoneOffset.UTC).toInstant();
		return Date.from(i);
	}

}
