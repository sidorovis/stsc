package stsc.distributed.hadoop.grid;

import java.io.IOException;

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

class SimulatorMapper extends Mapper<LongWritable, SimulatorSettingsWritable, SimulatorSettingsWritable, StatisticsWritable> {

	private final StockStorage stockStorage;

	public SimulatorMapper() {
		this.stockStorage = HadoopStaticDataSingleton.getStockStorage();
	}

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {

	}

	@Override
	protected void map(LongWritable key, SimulatorSettingsWritable value, Context context) throws java.io.IOException, InterruptedException {
		try {
			final SimulatorSettings simulatorSettings = value.getSimulatorSettings(stockStorage);
			final Simulator simulator = new Simulator(simulatorSettings);
			final Statistics statistics = simulator.getStatistics();
			context.write(value, new StatisticsWritable(statistics));
		} catch (BadAlgorithmException | BadSignalException e) {
			throw new InterruptedException(e.getMessage());
		}
	};
}
