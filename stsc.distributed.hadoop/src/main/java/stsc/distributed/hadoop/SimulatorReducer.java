package stsc.distributed.hadoop;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.RawKeyValueIterator;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.StatusReporter;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.task.ReduceContextImpl;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.distributed.hadoop.types.StatisticsWritable;
import stsc.distributed.hadoop.types.TradingStrategyWritable;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticList;
import stsc.general.statistic.Statistics;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StrategySelector;
import stsc.general.statistic.cost.function.CostWeightedProductFunction;
import stsc.general.strategy.TradingStrategy;
import stsc.general.testhelper.TestGeneticSimulatorSettings;

class SimulatorReducer extends Reducer<LongWritable, TradingStrategyWritable, LongWritable, TradingStrategyWritable> {

	private final StrategySelector strategySelector;

	public SimulatorReducer() {
		this.strategySelector = new StatisticsByCostSelector(100, new CostWeightedProductFunction());
	}

	@Override
	protected void reduce(LongWritable key, Iterable<TradingStrategyWritable> values, Context context) throws IOException, InterruptedException {
		TradingStrategyWritable tsw;
		try {
			tsw = new TradingStrategyWritable(new TradingStrategy(getSettings(), getStatistics()));
			context.write(key, tsw);
		} catch (BadAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private SimulatorSettings getSettings() throws BadAlgorithmException {
		final SimulatorSettingsGeneticList list = TestGeneticSimulatorSettings.getGeneticList();
		return list.generateRandom();
	}

	private Statistics getStatistics() {
		final Map<String, Double> list = new HashMap<>();
		list.put("getAvGain", 10.45);
		list.put("getAvWinAvLoss", 62.13);
		list.put("getPeriod", 16.0);
		return new Statistics(list);
	}

}
