package stsc.distributed.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;

public class StrategyGridRecordReader extends RecordReader<LongWritable, SimulatorSettingsGridList> {
 
	public StrategyGridRecordReader() {

	}

	@Override
	public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LongWritable getCurrentKey() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SimulatorSettingsGridList getCurrentValue() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
