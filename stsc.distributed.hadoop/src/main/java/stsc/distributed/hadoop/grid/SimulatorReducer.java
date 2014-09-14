package stsc.distributed.hadoop.grid;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Reducer;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.distributed.hadoop.types.StatisticsWritable;
import stsc.distributed.hadoop.types.TradingStrategyWritable;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StrategySelector;
import stsc.general.statistic.cost.function.CostWeightedProductFunction;
import stsc.general.strategy.TradingStrategy;

public class SimulatorReducer extends Reducer<LongWritable, TradingStrategyWritable, SimulatorSettingsWritable, StatisticsWritable> {

	private StockStorage stockStorage;
	private final StrategySelector strategySelector;

	public SimulatorReducer() throws IOException {
		final CostWeightedProductFunction cf = new CostWeightedProductFunction();
		cf.addParameter("getWinProb", 2.5);
		cf.addParameter("getAvLoss", -1.0);
		cf.addParameter("getAvWin", 1.0);
		cf.addParameter("getStartMonthAvGain", 1.2);
		cf.addParameter("ddDurationAvGain", -1.2);
		cf.addParameter("ddValueAvGain", -1.2);
		this.strategySelector = new StatisticsByCostSelector(150, cf);
	}

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		final FileSystem hdfs = FileSystem.get(context.getConfiguration());
		this.stockStorage = HadoopStaticDataSingleton.getStockStorage(hdfs, new Path(HadoopStaticDataSingleton.DATAFEED_HDFS_PATH));
	}

	@Override
	protected void reduce(LongWritable key, Iterable<TradingStrategyWritable> values, Context context) throws IOException, InterruptedException {
		try {
			for (TradingStrategyWritable ts : values) {
				ts.getTradingStrategy(stockStorage);
				strategySelector.addStrategy(ts.getTradingStrategy(stockStorage));
			}
			for (TradingStrategy ts : strategySelector.getStrategies()) {
				final SimulatorSettingsWritable ssw = new SimulatorSettingsWritable(ts.getSettings());
				final StatisticsWritable sw = new StatisticsWritable(ts.getStatistics());
				context.write(ssw, sw);
			}
		} catch (BadAlgorithmException e) {
			throw new InterruptedException(e.getMessage());
		}
	}
}
