package stsc.distributed.hadoop;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MapReduceExample extends Configured implements Tool {

	static class MyMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
		public MyMapper() {

		}

		protected void map(LongWritable key, Text value, org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, LongWritable, Text>.Context context)
				throws java.io.IOException, InterruptedException {
			context.getCounter("mygroup", "jeff").increment(1);
			context.write(key, value);
		};
	}

	@Override
	public int run(String[] args) throws Exception {
		Job job = new Job();
		job.setMapperClass(MyMapper.class);
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);
		return 0;
	}

	public static void main(String[] args) throws Exception {
		args = new String[] { "D:/in.txt", "D:/out.txt", "root@10.21.21.21:/tmp", "", "" + 22, "" + 600, "" + 2405,
				"" + "/var/db/host/privatekeys/" + "newsshcred" };
		ToolRunner.run(new MapReduceExample(), args);
	}
}