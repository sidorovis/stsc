package stsc.distributed.hadoop;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.general.testhelper.TestGridSimulatorSettings;

class GridRecordReader extends RecordReader<LongWritable, SimulatorSettingsWritable> {

	private LongWritable id;
	private Iterator<SimulatorSettings> iterator;
	private long size;

	public GridRecordReader(final SimulatorSettingsGridList list) {
		this.id = new LongWritable(0);
		this.iterator = list.iterator();
		this.size = list.size();
	}

	@Override
	public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
		// DO NOTHING
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		return iterator.hasNext();
	}

	@Override
	public LongWritable getCurrentKey() throws IOException, InterruptedException {
		final LongWritable result = id;
		id = new LongWritable(id.get() + 1);
		return result;
	}

	@Override
	public SimulatorSettingsWritable getCurrentValue() throws IOException, InterruptedException {
		final SimulatorSettings result = iterator.next();
		return new SimulatorSettingsWritable(result);
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		return ((float) id.get()) / size;
	}

	@Override
	public void close() throws IOException {
	}
}

class GridInputSplit extends InputSplit implements Writable {

	@Override
	public long getLength() throws IOException, InterruptedException {
		return 2;
	}

	@Override
	public String[] getLocations() throws IOException, InterruptedException {
		return new String[] { "main1", "main2" };
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(2);
		System.out.println("GridInputSplit.write");
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		final long l = in.readLong();
		System.out.println("GridInputSplit.readFields: " + l);
	}

}

class GridInputFormat extends InputFormat<LongWritable, SimulatorSettingsWritable> {

	final SimulatorSettingsGridList list;

	public GridInputFormat() {
		this.list = TestGridSimulatorSettings.getSmallGridList();
	}

	@Override
	public List<InputSplit> getSplits(JobContext context) throws IOException, InterruptedException {
		final GridInputSplit gis = new GridInputSplit();
		final List<InputSplit> splits = new ArrayList<InputSplit>();
		splits.add(gis);
		return splits;
	}

	@Override
	public RecordReader<LongWritable, SimulatorSettingsWritable> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException,
			InterruptedException {
		return new GridRecordReader(list);
	}

}
