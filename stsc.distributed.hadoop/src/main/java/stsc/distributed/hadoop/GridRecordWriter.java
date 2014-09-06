package stsc.distributed.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.LongWritable;
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
