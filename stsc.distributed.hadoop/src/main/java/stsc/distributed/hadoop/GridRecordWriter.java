package stsc.distributed.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputCommitter;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.distributed.hadoop.types.TradingStrategyWritable;

class GridRecordWriter extends RecordWriter<LongWritable, TradingStrategyWritable> {

	@Override
	public void write(LongWritable key, TradingStrategyWritable value) throws IOException, InterruptedException {
		System.out.println("OUT");
	}

	@Override
	public void close(TaskAttemptContext context) throws IOException, InterruptedException {
		System.out.println("close");

	}

}

class GridOutputFormat extends OutputFormat<LongWritable, TradingStrategyWritable> {

	@Override
	public RecordWriter<LongWritable, TradingStrategyWritable> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
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
