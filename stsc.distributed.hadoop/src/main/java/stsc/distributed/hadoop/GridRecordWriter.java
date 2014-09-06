package stsc.distributed.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.FileOutputCommitter;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

class GridRecordWriter extends RecordWriter<LongWritable, ArrayWritable> {

	@Override
	public void write(LongWritable key, ArrayWritable value) throws IOException, InterruptedException {
		System.out.println("OUT");
	}

	@Override
	public void close(TaskAttemptContext context) throws IOException, InterruptedException {
		System.out.println("close");

	}

}

class GridOutputFormat extends OutputFormat<LongWritable, ArrayWritable> {

	@Override
	public RecordWriter<LongWritable, ArrayWritable> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
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
