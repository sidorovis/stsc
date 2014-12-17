package stsc.general.trading;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.storage.ExecutionsStorage;
import stsc.storage.StockStorageFactory;

public class TradeProcessorInit implements Cloneable {

	private final BrokerImpl broker;
	private final FromToPeriod period;
	private final ExecutionsStorage executionsStorage;

	public TradeProcessorInit(final StockStorage stockStorage, final FromToPeriod period) {
		this.broker = new BrokerImpl(stockStorage);
		this.period = period;
		this.executionsStorage = new ExecutionsStorage();
	}

	public TradeProcessorInit(final StockStorage stockStorage, final FromToPeriod period, final ExecutionsStorage executionsStorage) {
		this.broker = new BrokerImpl(stockStorage);
		this.period = period;
		this.executionsStorage = executionsStorage;
	}

	public TradeProcessorInit(final StockStorage stockStorage, final FromToPeriod period, final String config) throws BadAlgorithmException {
		this.broker = new BrokerImpl(stockStorage);
		this.period = period;
		final ExecutionsLoader executionsLoader = new ExecutionsLoader(period, config);
		this.executionsStorage = executionsLoader.getExecutionsStorage();
	}

	public TradeProcessorInit(final File configPath) throws BadAlgorithmException {
		try {
			Properties p = loadProperties(configPath.getAbsolutePath());
			final Set<String> stockNamesSet = getStockSet(p);
			final String filterDataFolderPath = p.getProperty("Data.filter.folder");
			final StockStorage stockStorage = StockStorageFactory.createStockStorage(stockNamesSet, filterDataFolderPath);

			final File algsConfig = new File(p.getProperty("Executions.path", "./algs.ini"));
			final FromToPeriod period = new FromToPeriod(p);
			final ExecutionsLoader executionsLoader = new ExecutionsLoader(algsConfig, period);
			final ExecutionsStorage executionsStorage = executionsLoader.getExecutionsStorage();

			this.broker = new BrokerImpl(stockStorage);
			this.period = period;
			this.executionsStorage = executionsStorage;
		} catch (ClassNotFoundException | IOException | InterruptedException | ParseException e) {
			throw new BadAlgorithmException(e.getMessage());
		}
	}

	private TradeProcessorInit(final BrokerImpl broker, final FromToPeriod period, final ExecutionsStorage executionsStorage) {
		this.broker = new BrokerImpl(broker.getStockStorage());
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

	public BrokerImpl getBrokerImpl() {
		return broker;
	}

	public FromToPeriod getPeriod() {
		return period;
	}

	public ExecutionsStorage getExecutionsStorage() {
		return executionsStorage;
	}

	public String stringHashCode() {
		return getExecutionsStorage().stringHashCode();
	}

	@Override
	public String toString() {
		return getExecutionsStorage().toString();
	}

	@Override
	public TradeProcessorInit clone() {
		return new TradeProcessorInit(broker, period, getExecutionsStorage().clone());
	}

	public List<String> generateOutForStocks() {
		return getExecutionsStorage().generateOutForStocks();
	}

	public List<String> generateOutForEods() {
		return getExecutionsStorage().generateOutForEods();
	}
}
