package stsc.distributed.examples;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import stsc.distributed.hadoop.types.TradingStrategyWritable;

class MyReducer extends Reducer<Text, TradingStrategyWritable, Text, LongWritable> {

	@Override
	protected void reduce(Text key, Iterable<TradingStrategyWritable> values, Context context) throws IOException, InterruptedException {
		long sum = 0;
		for (TradingStrategyWritable v : values) {
			sum += (int) (v.getTradingStrategy().getStatistics().getAvGain());
		}
		context.write(key, new LongWritable(sum));
	}

}
