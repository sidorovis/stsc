package stsc.distributed.examples;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import stsc.distributed.hadoop.types.TradingStrategyWritable;

public class MapReduceExample extends Configured implements Tool {

	@SuppressWarnings("deprecation")
	@Override
	public int run(String[] args) {
		Job job;
		try {
			job = new Job(new JobConf(this.getConf()), "word_count");

			job.setJarByClass(MapReduceExample.class);

			job.setMapperClass(MyMapper.class);
			job.setReducerClass(MyReducer.class);
			// job.setReducerClass(MyFilter.class);

			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(TradingStrategyWritable.class);

			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);

			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);

			FileInputFormat.setInputPaths(job, new Path(args[0]));
			FileOutputFormat.setOutputPath(job, new Path(args[1]));

			job.waitForCompletion(true);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void main(String[] args) throws IOException {
		final String pathToOut = "test_data/a1.out";
		if (new File(pathToOut).exists()) {
			FileUtils.deleteDirectory(new File(pathToOut));
		}
		args = new String[] { "test_data/a1.txt", pathToOut };
		try {
			ToolRunner.run(new MapReduceExample(), args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}