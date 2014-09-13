package stsc.distributed.hadoop.grid;

import java.io.IOException;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import stsc.common.TimeTracker;
import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.distributed.hadoop.types.StatisticsWritable;

public class GridHadoopStarterExample extends Configured implements Tool {

	// private static void addJarToDistributedCache(Class<?> classToAdd,
	// Configuration conf) throws IOException {
	// final String jar =
	// classToAdd.getProtectionDomain().getCodeSource().getLocation().getPath();
	// final File jarFile = new File(jar);
	// final Path hdfsJar = new Path("/vagrant/package/" + jarFile.getName());
	// final FileSystem hdfs = FileSystem.get(conf);
	// hdfs.copyFromLocalFile(false, true, new Path(jar), hdfsJar);
	// DistributedCache.addFileToClassPath(hdfsJar, conf);
	// }

	@Override
	public int run(String[] args) throws Exception {
		final TimeTracker tt = new TimeTracker();

		HadoopStaticDataSingleton.getStockStorage();
		// HadoopStaticDataSingleton.getGridList();
		// addJarToDistributedCache(AlgorithmsStorage.class, this.getConf());

		final Job job = Job.getInstance(this.getConf());
		job.setJobName(GridHadoopStarterExample.class.getSimpleName());
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
		// Sysout tt
		tt.finish();
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
