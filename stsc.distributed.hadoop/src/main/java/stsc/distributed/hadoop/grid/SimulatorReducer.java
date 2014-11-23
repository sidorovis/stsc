package stsc.distributed.hadoop.grid;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Reducer;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.distributed.hadoop.types.StatisticsWritable;
import stsc.distributed.hadoop.types.TradingStrategyWritable;
import stsc.general.statistic.StrategySelector;
import stsc.general.strategy.TradingStrategy;

public class SimulatorReducer extends Reducer<LongWritable, TradingStrategyWritable, SimulatorSettingsWritable, StatisticsWritable> {

	private StockStorage stockStorage;

	public SimulatorReducer() throws IOException {
	}

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		final FileSystem hdfs = FileSystem.get(context.getConfiguration());
		this.stockStorage = HadoopSettings.getStockStorage(hdfs, HadoopSettings.getInstance().getHadoopDatafeedHdfsPath());
	}

	@Override
	protected void reduce(LongWritable key, Iterable<TradingStrategyWritable> values, Context context) throws IOException,
			InterruptedException {
		try {
			final StrategySelector ss = HadoopSettings.getInstance().strategySelector;
			for (TradingStrategyWritable ts : values) {
				ts.getTradingStrategy(stockStorage);
				ss.addStrategy(ts.getTradingStrategy(stockStorage));
			}
			for (TradingStrategy ts : ss.getStrategies()) {
				final SimulatorSettingsWritable ssw = new SimulatorSettingsWritable(ts.getSettings());
				final StatisticsWritable sw = new StatisticsWritable(ts.getStatistics());
				context.write(ssw, sw);
			}
		} catch (BadAlgorithmException e) {
			throw new InterruptedException(e.getMessage());
		}
	}
}
