package stsc.distributed.hadoop.grid;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

import stsc.algorithms.Input;
import stsc.algorithms.eod.primitive.OneSideOpenAlgorithm;
import stsc.algorithms.eod.primitive.PositionNDayMStocks;
import stsc.algorithms.stock.indices.primitive.Ema;
import stsc.algorithms.stock.indices.primitive.Level;
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
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StrategySelector;
import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.statistic.cost.function.CostWeightedProductFunction;
import stsc.storage.AlgorithmsStorage;
import stsc.storage.ThreadSafeStockStorage;

public class HadoopSettings {

	private static HadoopSettings hadoopSettings = new HadoopSettings();

	/**
	 * {@link HadoopSettings#copyOriginalDatafeedPath} boolean flag if true then
	 * we would try to copy original Datafeed (from FS) to HDFS.
	 */
	public boolean copyOriginalDatafeedPath = true;
	/**
	 * {@link #originalDatafeedPath} parameter is used for getting information
	 * where original Datafeed is placed on the FS
	 */
	public String originalDatafeedPath = "./test_data/";

	/**
	 * {@link HadoopSettings#datafeedHdfsPath} is a path to the Datafeed on HDFS
	 * (after copy)
	 */
	public String datafeedHdfsPath = "./yahoo_datafeed/";

	public String outputPathOnHdfs = "./output_data/";
	public String outputPathOnLocal = "./";
	public String outputFileName = "output.txt";

	public int inputSplitSize = 1;

	public String[] inputSplitLocations = new String[] { "this" };

	public CostFunction costFunction;
	public StrategySelector strategySelector;

	public boolean copyAnswerToLocal = true;

	// StockStorage part

	private StockStorage stockStorage = null;

	// SimulatorSettingsGrid part

	private SimulatorSettingsGridList simulatorSettingsGridList;

	private HadoopSettings() {
		costFunction = generateDefaultCostFunction();
		strategySelector = new StatisticsByCostSelector(150, costFunction);
	}

	private CostFunction generateDefaultCostFunction() {
		final CostWeightedProductFunction cf = new CostWeightedProductFunction();
		cf.addParameter("getWinProb", 2.5);
		cf.addParameter("getAvLoss", -1.0);
		cf.addParameter("getAvWin", 1.0);
		cf.addParameter("getStartMonthAvGain", 1.2);
		cf.addParameter("ddDurationAvGain", -1.2);
		cf.addParameter("ddValueAvGain", -1.2);
		return cf;
	}

	public Path getHadoopDatafeedHdfsPath() {
		return new Path(datafeedHdfsPath);
	}

	public Path getHdfsOutputPath() {
		return new Path(outputPathOnHdfs + outputFileName);
	}

	public Path getLocalOutputPath() {
		return new Path(outputPathOnLocal + outputFileName);
	}

	public static HadoopSettings getInstance() {
		return hadoopSettings;
	}

	public synchronized StockStorage getStockStorage(FileSystem hdfs) throws IOException {
		return getStockStorage(hdfs, getHadoopDatafeedHdfsPath());
	}

	public synchronized StockStorage getStockStorage(FileSystem hdfs, Path path) throws IOException {
		if (stockStorage == null) {
			stockStorage = new ThreadSafeStockStorage();
			final RemoteIterator<LocatedFileStatus> fileIterator = hdfs.listFiles(path, false);
			while (fileIterator.hasNext()) {
				final LocatedFileStatus lfs = fileIterator.next();
				try (final FSDataInputStream in = hdfs.open(lfs.getPath())) {
					stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile(in));
				}
			}
		}
		return stockStorage;
	}

	public synchronized StockStorage getStockStorage() throws IOException {
		if (stockStorage == null) {
			throw new IOException("Method getStockStorage() should be called only after getStockStorage(FileSystem, Path) version.");
		}
		return stockStorage;
	}

	public void setGridList(SimulatorSettingsGridList gridList) {
		this.simulatorSettingsGridList = gridList;
	}

	public SimulatorSettingsGridList getGridList() throws IOException {
		if (simulatorSettingsGridList != null) {
			return simulatorSettingsGridList;
		}
		return getDefaultSimulatorSettingsGridList();
	}

	private SimulatorSettingsGridList getDefaultSimulatorSettingsGridList() throws IOException {
		try {
			final FromToPeriod period = new FromToPeriod("01-01-2013", "01-01-2014");
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
		settings.addStock("in", algoStockName(Input.class.getSimpleName()), "e", Arrays.asList(new String[] { "open", "close" }));
		settings.addStock("ema", algoStockName(Ema.class.getSimpleName()),
				new AlgorithmSettingsIteratorFactory(period).add(new MpDouble("P", 0.1, 0.6, 0.5)).add(new MpSubExecution("", "in")));
		settings.addStock(
				"level",
				algoStockName("." + Level.class.getSimpleName()),
				new AlgorithmSettingsIteratorFactory(period).add(new MpDouble("f", 15.0, 20.0, 5)).add(
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
