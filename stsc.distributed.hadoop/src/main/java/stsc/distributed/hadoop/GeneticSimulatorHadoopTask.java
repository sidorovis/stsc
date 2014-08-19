package stsc.distributed.hadoop;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Progressable;
import org.joda.time.LocalDate;

import cascading.tap.hadoop.io.MultiInputSplit;
import stsc.common.BadSignalException;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpString;
import stsc.general.simulator.multistarter.MpSubExecution;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticFactory;
import stsc.general.statistic.Statistics;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StrategySelector;
import stsc.general.statistic.cost.function.WeightedSumCostFunction;
import stsc.general.strategy.TradingStrategy;
import stsc.storage.AlgorithmsStorage;

class GeneticSimulatorHadoopTask {

	private static String algoStockName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getStock(aname).getName();
	}

	private static String algoEodName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getEod(aname).getName();
	}

	static private SimulatorSettingsGeneticFactory getFactory() throws BadParameterException, BadAlgorithmException {
		final StockStorage stockStorage = StockStorageSingleton.getInstance();
		final LocalDate startOfPeriod = new LocalDate(2013, 1, 1);
		final LocalDate endOfPeriod = new LocalDate(2014, 1, 1);

		final FromToPeriod period = new FromToPeriod(startOfPeriod.toDate(), endOfPeriod.toDate());

		final SimulatorSettingsGeneticFactory settings = new SimulatorSettingsGeneticFactory(stockStorage, period);

		final AlgorithmSettingsIteratorFactory factoryIn = new AlgorithmSettingsIteratorFactory(settings.getPeriod());
		factoryIn.add(new MpString("e", new String[] { "open", "close" }));
		settings.addStock("in", algoStockName("In"), factoryIn);

		final AlgorithmSettingsIteratorFactory factoryEma = new AlgorithmSettingsIteratorFactory(settings.getPeriod());
		factoryEma.add(new MpDouble("P", 0.1, 1.11, 0.2));
		factoryEma.add(new MpSubExecution("", Arrays.asList(new String[] { "in" })));
		settings.addStock("ema", algoStockName("Ema"), factoryEma);

		final AlgorithmSettingsIteratorFactory factorySma = new AlgorithmSettingsIteratorFactory(settings.getPeriod());
		factorySma.add(new MpDouble("n", 5, 15, 1));
		factorySma.add(new MpSubExecution("", Arrays.asList(new String[] { "in" })));
		settings.addStock("sma", algoStockName("Sma"), factorySma);

		final AlgorithmSettingsIteratorFactory factoryPositionSide = new AlgorithmSettingsIteratorFactory(settings.getPeriod());
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "ema", "sma" })));
		factoryPositionSide.add(new MpInteger("n", 22, 250, 20));
		factoryPositionSide.add(new MpInteger("m", 20, 40, 2));
		factoryPositionSide.add(new MpDouble("ps", 2500.0, 50000.0, 2500.0));
		factoryPositionSide.add(new MpString("side", new String[] { "long", "short" }));

		return settings.addEod("pnm", algoEodName("PositionNDayMStocks"), factoryPositionSide);
	}

	public static class SimulatorSettingsCalculatingMap extends MapReduceBase
	//
			implements Mapper<Text, MapWritable, Text, MapWritable> {

		@Override
		public void map(Text key, MapWritable ss, OutputCollector<Text, MapWritable> output, Reporter reporter) throws IOException {
			// try {
			// final HashMap<String, String> v = new HashMap<>();
			// for (Entry<Writable,Writable> i : ss.entrySet()) {
			//
			// }
			// SimulatorSettings settings;
			// final Simulator simulator = new Simulator(settings);
			// final Statistics statistics = simulator.getStatistics();
			// final TradingStrategy strategy = new TradingStrategy(settings,
			// statistics);
			// output.collect(key, );
			// } catch (BadAlgorithmException | BadSignalException e) {
			// e.printStackTrace();
			// }
		}
	}

	public static class TradingStrategiesReduce extends MapReduceBase implements
			Reducer<SimulatorSettingsGeneticFactory, TradingStrategy, SimulatorSettingsGeneticFactory, List<TradingStrategy>> {

		@Override
		public void reduce(SimulatorSettingsGeneticFactory key, Iterator<TradingStrategy> values,
				OutputCollector<SimulatorSettingsGeneticFactory, List<TradingStrategy>> output, Reporter reporter) throws IOException {
			StrategySelector selector = new StatisticsByCostSelector(100, new WeightedSumCostFunction());
			while (values.hasNext()) {
				final TradingStrategy strategy = values.next();
				selector.addStrategy(strategy);
			}
			output.collect(key, selector.getStrategies());
		}
	}

	class InputRecordReader implements org.apache.hadoop.mapred.RecordReader<SimulatorSettingsGeneticFactory, SimulatorSettings> {

		SimulatorSettingsGeneticFactory factory;
		int N = 100;
		Integer n = 0;

		InputRecordReader(SimulatorSettingsGeneticFactory factory) {
			this.factory = factory;
		}

		@Override
		public boolean next(SimulatorSettingsGeneticFactory key, SimulatorSettings value) throws IOException {
			synchronized (n) {
				if (n == N) {
					return false;
				}
				++n;
			}
			return true;
		}

		@Override
		public SimulatorSettingsGeneticFactory createKey() {
			return factory;
		}

		@Override
		public SimulatorSettings createValue() {
			try {
				return factory.getList().generateRandom();
			} catch (BadAlgorithmException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public long getPos() throws IOException {
			return n;
		}

		@Override
		public void close() throws IOException {
		}

		@Override
		public float getProgress() throws IOException {
			return (float) ((1.0 * n) / N);
		}

	}

	class StatisticsInputFormat implements org.apache.hadoop.mapred.InputFormat<SimulatorSettingsGeneticFactory, SimulatorSettings> {

		InputRecordReader record;

		StatisticsInputFormat(SimulatorSettingsGeneticFactory factory) {
			try {
				record = new InputRecordReader(getFactory());
			} catch (BadParameterException | BadAlgorithmException e) {
				e.printStackTrace();
			}
		}

		@Override
		public InputSplit[] getSplits(JobConf job, int numSplits) throws IOException {
			MultiInputSplit a[] = {};
			return a;
		}

		@Override
		public InputRecordReader getRecordReader(InputSplit split, JobConf job, Reporter reporter) throws IOException {
			return record;
		}
	}

	class OutputRecordWriter implements RecordWriter<SimulatorSettingsGeneticFactory, List<TradingStrategy>> {

		@Override
		public void close(Reporter reporter) throws IOException {
		}

		@Override
		public void write(SimulatorSettingsGeneticFactory key, List<TradingStrategy> value) throws IOException {
			for (TradingStrategy tradingStrategy : value) {
				synchronized (this) {
					File file = new File("~/output/out.txt");
					PrintWriter writer = new PrintWriter(file);
					writer.println(tradingStrategy.getAvGain());
					writer.close();
				}
			}
		}
	}

	class StatisticsOutputFormat implements OutputFormat<SimulatorSettingsGeneticFactory, List<TradingStrategy>> {

		OutputRecordWriter writer = new OutputRecordWriter();

		@Override
		public OutputRecordWriter getRecordWriter(FileSystem ignored, JobConf job, String name, Progressable progress) throws IOException {
			return writer;
		}

		@Override
		public void checkOutputSpecs(FileSystem ignored, JobConf job) throws IOException {
		}

	}

	private void loadData() {
		try {
			AlgorithmsStorage.getInstance();
			StockStorageSingleton.getInstance("D:/dev/java/StscData/data/", "D:/dev/java/StscData/filtered_data");
		} catch (BadAlgorithmException | ClassNotFoundException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	GeneticSimulatorHadoopTask(final String path, final String filteredPath) {
		loadData();

		JobConf conf = new JobConf(GeneticSimulatorHadoopTask.class);
		conf.setJobName("generate_first_population");

		conf.setOutputKeyClass(SimulatorSettingsGeneticFactory.class);
		conf.setOutputValueClass(List.class);

		conf.setMapperClass(SimulatorSettingsCalculatingMap.class);

		conf.setCombinerClass(TradingStrategiesReduce.class);
		conf.setReducerClass(TradingStrategiesReduce.class);

		conf.setInputFormat(StatisticsInputFormat.class);
		conf.setOutputFormat(StatisticsOutputFormat.class);

		try {
			JobClient.runJob(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new GeneticSimulatorHadoopTask("D:/dev/java/StscData/data/", "D:/dev/java/StscData/filtered_data");
	}
}
