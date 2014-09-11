package stsc.distributed.hadoop.grid;

import java.io.IOException;

import org.apache.hadoop.mapred.FileOutputCommitter;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.distributed.hadoop.types.StatisticsWritable;

class GridRecordWriter extends RecordWriter<SimulatorSettingsWritable, StatisticsWritable> {

	@Override
	public void write(SimulatorSettingsWritable key, StatisticsWritable value) throws IOException, InterruptedException {
		System.out.println("OUT");
	}

	@Override
	public void close(TaskAttemptContext context) throws IOException, InterruptedException {
		System.out.println("close");

	}

}

class GridOutputFormat extends OutputFormat<SimulatorSettingsWritable, StatisticsWritable> {

	@Override
	public RecordWriter<SimulatorSettingsWritable, StatisticsWritable> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
		return new GridRecordWriter();
	}

	@Override
	public void checkOutputSpecs(JobContext context) throws IOException, InterruptedException {
	}

	@Override
	public OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException, InterruptedException {
		return new FileOutputCommitter();
	}

}
