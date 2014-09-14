package stsc.distributed.hadoop.grid;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;

public class GridRecordReader extends RecordReader<LongWritable, SimulatorSettingsWritable> {
	private long size;
	private long id = 0;
	private Iterator<SimulatorSettings> iterator;
	private SimulatorSettings current;
	private boolean finished;

	public GridRecordReader(final FileSystem hdfs, Path path) throws IOException {
		HadoopStaticDataSingleton.getStockStorage(hdfs, path);
		final SimulatorSettingsGridList list = HadoopStaticDataSingleton.getGridList();
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
		iterator = null;
		current = null;
	}
}
