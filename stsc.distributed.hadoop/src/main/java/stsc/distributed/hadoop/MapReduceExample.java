package stsc.distributed.hadoop;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MapReduceExample extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		Job job = new Job(new JobConf(this.getConf()), "word_count");

		job.setJarByClass(MapReduceExample.class);
		job.setMapperClass(MyMapper.class);

		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);
		return 0;
	}

	public static void main(String[] args) throws Exception {
		args = new String[] { "in.txt", "out.txt" };
		ToolRunner.run(new MapReduceExample(), args);
	}
}