package stsc.distributed.examples;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.distributed.hadoop.grid.HadoopStaticDataSingleton;
import stsc.distributed.hadoop.types.TradingStrategyWritable;

class MyReducer extends Reducer<Text, TradingStrategyWritable, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<TradingStrategyWritable> values, Context context) throws IOException, InterruptedException {
		String stats = "";
		final StockStorage ss = HadoopStaticDataSingleton.getStockStorage();
		for (TradingStrategyWritable v : values) {
			try {
				stats = (v.getTradingStrategy(ss).getStatistics()).toString();
			} catch (BadAlgorithmException e) {
				throw new IOException(e.getMessage());
			}
		}
		context.write(key, new Text(stats));
	}

}
