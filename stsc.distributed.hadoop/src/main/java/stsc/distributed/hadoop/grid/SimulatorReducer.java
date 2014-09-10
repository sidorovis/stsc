package stsc.distributed.hadoop.grid;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Reducer;
import stsc.distributed.hadoop.types.TradingStrategyWritable;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StrategySelector;
import stsc.general.statistic.cost.function.CostWeightedProductFunction;
import stsc.general.strategy.TradingStrategy;

class SimulatorReducer extends Reducer<LongWritable, TradingStrategyWritable, LongWritable, TradingStrategyWritable> {

	private final StrategySelector strategySelector;

	public SimulatorReducer() {
		this.strategySelector = new StatisticsByCostSelector(100, new CostWeightedProductFunction());
	}

	@Override
	protected void reduce(LongWritable key, Iterable<TradingStrategyWritable> values, Context context) throws IOException, InterruptedException {
		for (TradingStrategyWritable tradingStrategyWritable : values) {
			strategySelector.addStrategy(tradingStrategyWritable.getTradingStrategy());
		}
		strategySelector.getStrategies();
		for (TradingStrategy ts : strategySelector.getStrategies()) {
			context.write(new LongWritable(ts.getSettings().getId()), new TradingStrategyWritable(ts));
		}
	}

}
