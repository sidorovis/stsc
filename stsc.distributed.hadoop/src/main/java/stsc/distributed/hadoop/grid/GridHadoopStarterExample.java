package stsc.distributed.hadoop.grid;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import stsc.common.TimeTracker;
import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.distributed.hadoop.types.StatisticsWritable;
import stsc.distributed.hadoop.types.TradingStrategyWritable;

public class GridHadoopStarterExample extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {

		final Job job = Job.getInstance(this.getConf());
		checkAndCopyDatafeed("./test_data/", HadoopStaticDataSingleton.DATAFEED_HDFS_PATH);

		job.setJobName(GridHadoopStarterExample.class.getSimpleName());
		job.setJarByClass(GridHadoopStarterExample.class);

		job.setMapperClass(SimulatorMapper.class);
		job.setReducerClass(SimulatorReducer.class);

		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(TradingStrategyWritable.class);

		job.setOutputKeyClass(SimulatorSettingsWritable.class);
		job.setOutputValueClass(StatisticsWritable.class);

		job.setInputFormatClass(GridInputFormat.class);
		job.setOutputFormatClass(GridOutputFormat.class);

		job.waitForCompletion(true);
		copyAnswerToLocal();
		return 0;
	}

	private void checkAndCopyDatafeed(String localPath, String hdfsPath) throws IOException {
		final FileSystem hdfs = FileSystem.get(this.getConf());
		if (!hdfs.exists(new Path(hdfsPath))) {
			hdfs.mkdirs(new Path(hdfsPath));
			File folder = new File(localPath);
			File[] listOfFiles = folder.listFiles();
			for (File file : listOfFiles) {
				hdfs.copyFromLocalFile(new Path(file.getPath()), new Path(hdfsPath + file.getName()));
			}
		}
	}

	private void copyAnswerToLocal() throws IllegalArgumentException, IOException {
		final FileSystem hdfs = FileSystem.get(this.getConf());
		final Path out = new Path(GridOutputFormat.OUT_PATH + GridRecordWriter.FILE_NAME);
		if (hdfs.exists(out)) {
			hdfs.copyToLocalFile(true, out, new Path(new File("." + GridRecordWriter.FILE_NAME).getPath()), true);
		}
	}

	public static void main(String[] args) throws IOException {
		try {
			ToolRunner.run(new GridHadoopStarterExample(), args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
