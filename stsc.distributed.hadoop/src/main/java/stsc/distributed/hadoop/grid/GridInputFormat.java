package stsc.distributed.hadoop.grid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import stsc.distributed.hadoop.types.SimulatorSettingsWritable;

public class GridInputFormat extends InputFormat<LongWritable, SimulatorSettingsWritable> {

	@Override
	public List<InputSplit> getSplits(JobContext context) throws IOException, InterruptedException {
		final List<InputSplit> splits = new ArrayList<InputSplit>(0);
		splits.add(new GridInputSplit());
		return splits;
	}

	@Override
	public RecordReader<LongWritable, SimulatorSettingsWritable> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException,
			InterruptedException {
		final FileSystem hdfs = FileSystem.get(context.getConfiguration());
		return new GridRecordReader(hdfs, new Path(HadoopStaticDataSingleton.DATAFEED_HDFS_PATH));
	}

}