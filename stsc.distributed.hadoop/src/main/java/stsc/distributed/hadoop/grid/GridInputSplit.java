package stsc.distributed.hadoop.grid;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.commons.lang.Validate;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputSplit;

public class GridInputSplit extends InputSplit implements Writable {

	@Override
	public long getLength() throws IOException, InterruptedException {
		return 2;
	}

	@Override
	public String[] getLocations() throws IOException, InterruptedException {
		return new String[] { "this", "this" };
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF("thisthis");
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		final String thisValue = in.readUTF();
		Validate.isTrue(thisValue.equals("thisthis"), "should be 'thisthis'");
	}

}