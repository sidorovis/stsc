package stsc.distributed.hadoop.grid;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.Validate;
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

class GridRecordReader extends RecordReader<LongWritable, SimulatorSettingsWritable> {

	private long size;
	private long id = 0;
	private Iterator<SimulatorSettings> iterator;
	private SimulatorSettings current;
	private boolean finished;

	public GridRecordReader(final SimulatorSettingsGridList list) {
		this.iterator = list.iterator();
		this.size = list.size();
		this.finished = !iterator.hasNext();
		this.current = iterator.next();
	}

	@Override
	public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
		// DO NOTHING
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		return !finished;
	}

	@Override
	public LongWritable getCurrentKey() throws IOException, InterruptedException {
		return new LongWritable(current.getId());
	}

	@Override
	public SimulatorSettingsWritable getCurrentValue() throws IOException, InterruptedException {
		if (iterator.hasNext()) {
			final SimulatorSettings result = current;
			current = iterator.next();
			id = current.getId();
			return new SimulatorSettingsWritable(result);
		} else {
			finished = true;
			return new SimulatorSettingsWritable(current);
		}
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		return ((float) id) / size;
	}

	@Override
	public void close() throws IOException {
	}
}

class GridInputSplit extends InputSplit implements Writable {

	@Override
	public long getLength() throws IOException, InterruptedException {
		return 1;
	}

	@Override
	public String[] getLocations() throws IOException, InterruptedException {
		return new String[] { "this" };
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF("this");
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		final String thisValue = in.readUTF();
		Validate.isTrue(thisValue.equals("this"), "should be 'this'");
	}

}

class GridInputFormat extends InputFormat<LongWritable, SimulatorSettingsWritable> {

	final SimulatorSettingsGridList list;

	public GridInputFormat() {
		this.list = HadoopStaticDataSingleton.getGridList();
		Validate.notNull(this.list, "SimulatorSettingsGridList should not be null for " + GridInputFormat.class.getName());
	}

	@Override
	public List<InputSplit> getSplits(JobContext context) throws IOException, InterruptedException {
		final List<InputSplit> splits = new ArrayList<InputSplit>(0);
		// for (int i = 0; i < list.size(); i++) {
		splits.add(new GridInputSplit());
		// }
		return splits;
	}

	@Override
	public RecordReader<LongWritable, SimulatorSettingsWritable> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException,
			InterruptedException {
		return new GridRecordReader(list);
	}

}
