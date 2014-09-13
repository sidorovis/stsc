package stsc.distributed.hadoop.grid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;

public class GridInputFormat extends InputFormat<LongWritable, SimulatorSettingsWritable> {

	final SimulatorSettingsGridList list;

	public GridInputFormat() {
		this.list = HadoopStaticDataSingleton.getGridList();
		Validate.notNull(this.list, "SimulatorSettingsGridList should not be null for " + GridInputFormat.class.getName());
	}

	@Override
	public List<InputSplit> getSplits(JobContext context) throws IOException, InterruptedException {
		final List<InputSplit> splits = new ArrayList<InputSplit>(0);
		splits.add(new GridInputSplit());
		splits.add(new GridInputSplit());
		return splits;
	}

	@Override
	public RecordReader<LongWritable, SimulatorSettingsWritable> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException,
			InterruptedException {
		return new GridRecordReader(list);
	}

}