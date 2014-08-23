package stsc.distributed.hadoop;

import java.io.IOException;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MapFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MapReduceExample extends Configured implements Tool {

	@SuppressWarnings("deprecation")
	@Override
	public int run(String[] args) {
		Job job;
		try {
			job = new Job(new JobConf(this.getConf()), "word_count");

			job.setJarByClass(MapReduceExample.class);

			job.setMapperClass(MyMapper.class);
			job.setCombinerClass(MyReducer.class);
			job.setReducerClass(MyFilter.class);
			
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(LongWritable.class);
			
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
		// if (new File("out").exists()) {
		// FileUtils.deleteDirectory(new File("out"));
		// }
		args = new String[] { "in.txt", "out" };
		try {
			ToolRunner.run(new MapReduceExample(), args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}