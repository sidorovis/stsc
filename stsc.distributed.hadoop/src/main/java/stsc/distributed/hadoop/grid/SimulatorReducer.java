package stsc.distributed.hadoop.grid;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Reducer;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.distributed.hadoop.types.StatisticsWritable;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StrategySelector;
import stsc.general.statistic.cost.function.CostWeightedProductFunction;
import stsc.general.strategy.TradingStrategy;

class SimulatorReducer extends Reducer<SimulatorSettingsWritable, StatisticsWritable, SimulatorSettingsWritable, StatisticsWritable> {

	private final StockStorage stockStorage;
	private final StrategySelector strategySelector;

	public SimulatorReducer() {
		this.stockStorage = HadoopStaticDataSingleton.getStockStorage();
		this.strategySelector = new StatisticsByCostSelector(100, new CostWeightedProductFunction());
	}

	@Override
	protected void reduce(SimulatorSettingsWritable key, Iterable<StatisticsWritable> values, Context context) throws IOException, InterruptedException {
		try {
			for (StatisticsWritable statistics : values) {
				strategySelector.addStrategy(new TradingStrategy(key.getSimulatorSettings(stockStorage), statistics.getStatistics()));
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
