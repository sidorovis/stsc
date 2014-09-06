package stsc.distributed.hadoop;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;

public class HadoopStarterExample extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		@SuppressWarnings("deprecation")
		final Job job = new Job(new JobConf(this.getConf()), "StscOnHadoopExample");
		job.setJarByClass(HadoopStarterExample.class);

		job.setMapperClass(SimulatorMapper.class);
		job.setReducerClass(SimulatorReducer.class);

		job.waitForCompletion(true);
		return 0;
	}

}
