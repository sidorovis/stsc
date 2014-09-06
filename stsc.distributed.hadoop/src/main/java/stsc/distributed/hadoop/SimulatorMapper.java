package stsc.distributed.hadoop;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

import stsc.common.BadSignalException;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.distributed.hadoop.types.StatisticsWritable;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.Statistics;

public class SimulatorMapper extends Mapper<LongWritable, SimulatorSettingsWritable, LongWritable, StatisticsWritable> {

	final private StockStorage stockStorage;

	public SimulatorMapper(final StockStorage stockStorage) {
		this.stockStorage = stockStorage;
	}

	@Override
	protected void map(LongWritable key, SimulatorSettingsWritable value,
			org.apache.hadoop.mapreduce.Mapper<LongWritable, SimulatorSettingsWritable, LongWritable, StatisticsWritable>.Context context)
			throws java.io.IOException, InterruptedException {
		try {
			final SimulatorSettings simulatorSettings = value.getSimulatorSettings(stockStorage);
			final Simulator simulator = new Simulator(simulatorSettings);
			final Statistics statistics = simulator.getStatistics();
			context.write(key, new StatisticsWritable(statistics));
		} catch (BadAlgorithmException | BadSignalException e) {
			throw new InterruptedException(e.getMessage());
		}
	};

}
