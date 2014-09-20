package stsc.distributed.hadoop.grid;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import stsc.algorithms.In;
import stsc.algorithms.eod.primitive.OneSideOpenAlgorithm;
import stsc.algorithms.eod.primitive.PositionNDayMStocks;
import stsc.algorithms.stock.factors.primitive.Ema;
import stsc.algorithms.stock.factors.primitive.Level;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpSubExecution;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridFactory;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.storage.AlgorithmsStorage;
import stsc.storage.ThreadSafeStockStorage;
import stsc.yahoo.YahooFileStockStorage;

public class HadoopStaticDataSingleton {

	// StockStorage

	public static String DATAFEED_HDFS_PATH = "./yahoo_datafeed/";

	private static StockStorage stockStorage = null;

	public static StockStorage getStockStorage(final String dataFolder, final String filteredDataFolder) throws ClassNotFoundException,
			IOException, InterruptedException {
		if (stockStorage == null) {
			final YahooFileStockStorage yahooStockStorage = new YahooFileStockStorage(dataFolder, filteredDataFolder);
			stockStorage = yahooStockStorage;
			yahooStockStorage.waitForLoad();
		}
		return stockStorage;
	}

	public static StockStorage getStockStorage(FileSystem hdfs, Path path) throws IOException {
		if (stockStorage == null) {
			stockStorage = new ThreadSafeStockStorage();
			try (final FSDataInputStream in = hdfs.open(new Path(path + "/aapl.uf"))) {
				stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile(in));
			}
			try (final FSDataInputStream in = hdfs.open(new Path(path + "/adm.uf"))) {
				stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile(in));
			}
			try (final FSDataInputStream in = hdfs.open(new Path(path + "/spy.uf"))) {
				stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile(in));
			}
		}
		return stockStorage;
	}

	public static StockStorage getStockStorage() throws IOException {
		if (stockStorage == null) {
			stockStorage = new ThreadSafeStockStorage();
			stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/" + "aapl.uf"));
			stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/" + "adm.uf"));
			stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/" + "spy.uf"));
		}
		return stockStorage;
	}

	// SimulatorSettingsGridList

	private static SimulatorSettingsGridList simulatorSettingsGridList;

	public static SimulatorSettingsGridList getGridList() throws IOException {
		if (simulatorSettingsGridList != null) {
			return simulatorSettingsGridList;
		}
		try {
			final FromToPeriod period = new FromToPeriod("01-01-2000", "01-01-2014");
			final SimulatorSettingsGridFactory factory = new SimulatorSettingsGridFactory(getStockStorage(), period);
			fillFactory(period, factory);
			simulatorSettingsGridList = factory.getList();
			return simulatorSettingsGridList;
		} catch (ParseException | BadParameterException | BadAlgorithmException e) {
			throw new IOException(e.getMessage());
		}
	}

	private static void fillFactory(FromToPeriod period, SimulatorSettingsGridFactory settings) throws BadParameterException,
			BadAlgorithmException {
		settings.addStock("in", algoStockName(In.class.getSimpleName()), "e", Arrays.asList(new String[] { "open", "close" }));
		settings.addStock("ema", algoStockName(Ema.class.getSimpleName()),
				new AlgorithmSettingsIteratorFactory(period).add(new MpDouble("P", 0.1, 0.6, 0.1)).add(new MpSubExecution("", "in")));
		settings.addStock(
				"level",
				algoStockName(Level.class.getSimpleName()),
				new AlgorithmSettingsIteratorFactory(period).add(new MpDouble("f", 15.0, 20.0, 1)).add(
						new MpSubExecution("", Arrays.asList(new String[] { "ema" }))));
		settings.addEod("os", algoEodName(OneSideOpenAlgorithm.class.getSimpleName()), "side",
				Arrays.asList(new String[] { "long", "short" }));

		final AlgorithmSettingsIteratorFactory factoryPositionSide = new AlgorithmSettingsIteratorFactory(period);
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "in", "level" })));
		factoryPositionSide.add(new MpInteger("n", 1, 32, 32));
		factoryPositionSide.add(new MpInteger("m", 1, 32, 32));
		factoryPositionSide.add(new MpDouble("ps", 50000.0, 200001.0, 160000.0));
		settings.addEod("pnm", algoEodName(PositionNDayMStocks.class.getSimpleName()), factoryPositionSide);
	}

	private static String algoStockName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getStock(aname).getName();
	}

	private static String algoEodName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getEod(aname).getName();
	}

}