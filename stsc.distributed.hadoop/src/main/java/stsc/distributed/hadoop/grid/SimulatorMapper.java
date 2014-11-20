package stsc.distributed.hadoop.grid;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

import stsc.common.BadSignalException;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.distributed.hadoop.types.TradingStrategyWritable;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.Statistics;
import stsc.general.strategy.TradingStrategy;

public class SimulatorMapper extends Mapper<LongWritable, SimulatorSettingsWritable, LongWritable, TradingStrategyWritable> {

	private final LongWritable zero = new LongWritable(0);

	private StockStorage stockStorage;

	public SimulatorMapper() throws IOException {
	}

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		final FileSystem hdfs = FileSystem.get(context.getConfiguration());
		this.stockStorage = HadoopStaticDataSingleton.getStockStorage(hdfs, HadoopSettings.getInstance().getHadoopHdfsPath());
	}

	@Override
	protected void map(LongWritable key, SimulatorSettingsWritable value, Context context) throws java.io.IOException, InterruptedException {
		try {
			final SimulatorSettings settings = value.getSimulatorSettings(stockStorage);
			final Simulator simulator = new Simulator(settings);
			final Statistics statistics = simulator.getStatistics();
			final TradingStrategy tradingStrategy = new TradingStrategy(settings, statistics);
			context.write(zero, new TradingStrategyWritable(tradingStrategy));
		} catch (BadAlgorithmException | BadSignalException e) {
			throw new IOException(e.getMessage());
		}
	};
}
