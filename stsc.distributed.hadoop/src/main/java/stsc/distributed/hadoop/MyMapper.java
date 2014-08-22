package stsc.distributed.hadoop;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MyMapper extends Mapper<LongWritable, Text, LongWritable, Text> {

	public MyMapper() {

	}

	protected void map(LongWritable key, Text value, Context context) throws java.io.IOException, InterruptedException {
		context.getCounter("mygroup", "jeff").increment(1);
		context.write(key, value);
	};
}
