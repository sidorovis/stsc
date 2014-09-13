package stsc.distributed.examples;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import stsc.common.BadSignalException;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
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
			final StockStorage ssto = HadoopStaticDataSingleton.getStockStorage();
			final SimulatorSettings ss = HadoopStaticDataSingleton.getGridList().iterator().next();
			final Simulator simulator = new Simulator(ss);
			final Statistics s = simulator.getStatistics();
			final TradingStrategy ts = new TradingStrategy(ss, s);
			if (ssto.getStock("aapl").getDays().size() == 0) {
			} else {
				throw new BadAlgorithmException("APLES: " + String.valueOf(HadoopStaticDataSingleton.getGridList().size()) + "\n" + ss.stringHashCode() + "\n"
						+ ss.toString() + "\n" + s.toString());
			}
			context.write(value, new TradingStrategyWritable(ts));
		} catch (BadAlgorithmException | BadSignalException e) {
			throw new IOException(e.getMessage());
		}
	};
}
