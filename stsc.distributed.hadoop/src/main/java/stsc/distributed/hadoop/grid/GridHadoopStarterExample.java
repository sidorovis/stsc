package stsc.distributed.hadoop.grid;

import java.io.IOException;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import stsc.common.TimeTracker;
import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.distributed.hadoop.types.StatisticsWritable;

public class GridHadoopStarterExample extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		final TimeTracker tt = new TimeTracker();
		@SuppressWarnings("deprecation")
		final Job job = new Job(new JobConf(this.getConf()), "StscOnHadoopExample");
		job.setJarByClass(GridHadoopStarterExample.class);

		job.setInputFormatClass(GridInputFormat.class);
		job.setOutputFormatClass(GridOutputFormat.class);

		job.setMapOutputKeyClass(SimulatorSettingsWritable.class);
		job.setMapOutputValueClass(StatisticsWritable.class);

		job.setOutputKeyClass(SimulatorSettingsWritable.class);
		job.setOutputValueClass(StatisticsWritable.class);

		job.setMapperClass(SimulatorMapper.class);
		job.setReducerClass(SimulatorReducer.class);

		job.waitForCompletion(true);
		System.out.println(TimeTracker.lengthInSeconds(tt.finish()));
		return 0;
	}

	public static void main(String[] args) throws IOException {
		try {
			ToolRunner.run(new GridHadoopStarterExample(), args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
