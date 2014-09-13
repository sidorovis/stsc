package stsc.distributed.examples;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import stsc.common.BadSignalException;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.distributed.hadoop.grid.HadoopStaticDataSingleton;
import stsc.distributed.hadoop.types.TradingStrategyWritable;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.Statistics;
import stsc.general.strategy.TradingStrategy;

class MyMapper extends Mapper<LongWritable, Text, Text, TradingStrategyWritable> {

	public MyMapper() {
	}

	@Override
	protected void map(LongWritable key, Text value, Context context) throws java.io.IOException, InterruptedException {
		try {
			final SimulatorSettings ss = HadoopStaticDataSingleton.getGridList().iterator().next();
			final Statistics s = new Simulator(ss).getStatistics();
			final TradingStrategy ts = new TradingStrategy(ss, s);
			context.write(value, new TradingStrategyWritable(ts));
		} catch (BadAlgorithmException | BadSignalException e) {
			throw new IOException(e.getMessage());
		}
	};
}
