package stsc.distributed.hadoop;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import stsc.common.BadSignalException;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.distributed.hadoop.types.TradingStrategyWritable;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticList;
import stsc.general.statistic.Statistics;
import stsc.general.strategy.TradingStrategy;
import stsc.general.testhelper.TestGeneticSimulatorSettings;

class SimulatorMapper extends Mapper<LongWritable, SimulatorSettingsWritable, LongWritable, TradingStrategyWritable> {

	final private StockStorage stockStorage;

	public SimulatorMapper() {
		this.stockStorage = StockStorageSingleton.getInstance();
	}

	@Override
	protected void map(LongWritable key, SimulatorSettingsWritable value, Context context) throws java.io.IOException, InterruptedException {
		try {
			final SimulatorSettings simulatorSettings = getSettings(); // value.getSimulatorSettings(stockStorage);
			final Simulator simulator = new Simulator(simulatorSettings);
			final Statistics statistics = simulator.getStatistics();
			final TradingStrategy ts = new TradingStrategy(simulatorSettings, statistics);
			final TradingStrategyWritable tsw = new TradingStrategyWritable(ts);
			context.write(key, tsw);
		} catch (BadAlgorithmException | BadSignalException e) {
			throw new InterruptedException(e.getMessage());
		}
	};

	private SimulatorSettings getSettings() throws BadAlgorithmException {
		final SimulatorSettingsGeneticList list = TestGeneticSimulatorSettings.getGeneticList();
		return list.generateRandom();
	}

}
