package stsc.distributed.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.MapContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.map.WrappedMapper;

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

	public SimulatorMapper() {
	}

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {

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

class SimuatorMapContext extends WrappedMapper<LongWritable, SimulatorSettingsWritable, LongWritable, TradingStrategyWritable>.Context {

	public SimuatorMapContext(WrappedMapper<LongWritable, SimulatorSettingsWritable, LongWritable, TradingStrategyWritable> wrappedMapper,
			MapContext<LongWritable, SimulatorSettingsWritable, LongWritable, TradingStrategyWritable> mapContext) {
		wrappedMapper.super(mapContext);
		// TODO Auto-generated constructor stub
	}

}
