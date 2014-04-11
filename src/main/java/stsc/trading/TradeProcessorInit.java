package stsc.trading;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import stsc.common.FromToPeriod;
import stsc.storage.ExecutionsStorage;
import stsc.storage.StockStorage;
import stsc.storage.StockStorageFactory;

public class TradeProcessorInit {

	private final Broker broker;
	private final FromToPeriod period;
	private final ExecutionsStorage executionsStorage;

	public TradeProcessorInit(final StockStorage stockStorage, final FromToPeriod period,
			final ExecutionsStorage executionsStorage) {
		this.broker = new Broker(stockStorage);
		this.period = period;
		this.executionsStorage = executionsStorage;
	}

	public TradeProcessorInit(final String configPath) throws Exception {
		final Properties p = loadProperties(configPath);
		final Set<String> stockNamesSet = getStockSet(p);
		final String filterDataFolderPath = p.getProperty("Data.filter.folder");
		final StockStorage stockStorage = StockStorageFactory.createStockStorage(stockNamesSet, filterDataFolderPath);

		final String algsConfig = p.getProperty("Executions.path", "./algs.ini");
		final FromToPeriod period = new FromToPeriod(p);
		final ExecutionsLoader executionsLoader = new ExecutionsLoader(algsConfig, period);
		final ExecutionsStorage executionsStorage = executionsLoader.getExecutionsStorage();

		this.broker = new Broker(stockStorage);
		this.period = period;
		this.executionsStorage = executionsStorage;
	}

	private Set<String> getStockSet(final Properties p) {
		final String[] rawStockSet = p.getProperty("Stocks").split(",");
		Set<String> stockSet = new HashSet<>();
		for (String string : rawStockSet) {
			stockSet.add(string.trim());
		}
		return stockSet;
	}

	private Properties loadProperties(final String configPath) throws ClassNotFoundException, IOException {
		final Properties properties = new Properties();
		try (FileInputStream in = new FileInputStream(configPath)) {
			properties.load(in);
		}
		return properties;
	}

	public Broker getBroker() {
		return broker;
	}

	public FromToPeriod getPeriod() {
		return period;
	}

	public ExecutionsStorage getExecutionsStorage() {
		return executionsStorage;
	}

}
