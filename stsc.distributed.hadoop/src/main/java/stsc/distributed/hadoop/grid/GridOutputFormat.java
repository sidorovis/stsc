package stsc.distributed.hadoop.grid;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;

import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.distributed.hadoop.types.StatisticsWritable;

public class GridOutputFormat extends OutputFormat<SimulatorSettingsWritable, StatisticsWritable> {

	@Override
	public RecordWriter<SimulatorSettingsWritable, StatisticsWritable> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
		return new GridRecordWriter();
	}

	@Override
	public void checkOutputSpecs(JobContext context) throws IOException, InterruptedException {
	}

	@Override
	public OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException, InterruptedException {
		return new FileOutputCommitter(new Path("/user/vagrant/test_out"), context);
	}

}
