package stsc.distributed.hadoop.grid;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputSplit;

public class GridInputSplit extends InputSplit implements Writable {

	@Override
	public long getLength() throws IOException, InterruptedException {
		return HadoopSettings.getInstance().inputSplitLocations.length;
	}

	@Override
	public String[] getLocations() throws IOException, InterruptedException {
		return HadoopSettings.getInstance().inputSplitLocations;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(HadoopSettings.getInstance().inputSplitLocations.length);
		for (String s : HadoopSettings.getInstance().inputSplitLocations) {
			out.writeUTF(s);
		}
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		final Long size = in.readLong();
		for (int i = 0; i < size; ++i) {
			in.readUTF();
		}
	}

}