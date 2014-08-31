package stsc.distributed.hadoop.types;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.io.Writable;

public class MapEasyWritable implements Writable {

	protected final Map<String, String> strings;
	protected final Map<String, Long> longs;
	protected final Map<String, Integer> integers;
	protected final Map<String, Boolean> booleans;
	protected final Map<String, Double> doubles;

	protected MapEasyWritable() {
		this.strings = new HashMap<>();
		this.longs = new HashMap<>();
		this.integers = new HashMap<>();
		this.booleans = new HashMap<>();
		this.doubles = new HashMap<>();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		writeStrings(out);
		writeLongs(out);
		writeIntegers(out);
		writeBooleans(out);
		writeDoubles(out);
	}

	private void writeStrings(DataOutput out) throws IOException {
		out.writeLong(strings.size());
		for (Entry<String, String> s : strings.entrySet()) {
			out.writeUTF(s.getKey());
			out.writeUTF(s.getValue());
		}
	}

	private void writeLongs(DataOutput out) throws IOException {
		out.writeLong(longs.size());
		for (Entry<String, Long> s : longs.entrySet()) {
			out.writeUTF(s.getKey());
			out.writeLong(s.getValue());
		}
	}

	private void writeIntegers(DataOutput out) throws IOException {
		out.writeLong(integers.size());
		for (Entry<String, Integer> s : integers.entrySet()) {
			out.writeUTF(s.getKey());
			out.writeInt(s.getValue());
		}
	}

	private void writeBooleans(DataOutput out) throws IOException {
		out.writeLong(booleans.size());
		for (Entry<String, Boolean> s : booleans.entrySet()) {
			out.writeUTF(s.getKey());
			out.writeBoolean(s.getValue());
		}
	}

	private void writeDoubles(DataOutput out) throws IOException {
		out.writeLong(doubles.size());
		for (Entry<String, Double> s : doubles.entrySet()) {
			out.writeUTF(s.getKey());
			out.writeDouble(s.getValue());
		}
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		readStrings(in);
		readLongs(in);
		readIntegers(in);
		readBooleans(in);
		readDoubles(in);
	}

	private void readStrings(DataInput in) throws IOException {
		final long sizeOfCollection = in.readLong();
		for (long i = 0; i < sizeOfCollection; ++i) {
			final String key = in.readUTF();
			final String value = in.readUTF();
			strings.put(key, value);
		}
	}

	private void readLongs(DataInput in) throws IOException {
		final long sizeOfCollection = in.readLong();
		for (long i = 0; i < sizeOfCollection; ++i) {
			final String key = in.readUTF();
			final Long value = in.readLong();
			longs.put(key, value);
		}
	}

	private void readIntegers(DataInput in) throws IOException {
		final long sizeOfCollection = in.readLong();
		for (long i = 0; i < sizeOfCollection; ++i) {
			final String key = in.readUTF();
			final Integer value = in.readInt();
			integers.put(key, value);
		}
	}

	private void readBooleans(DataInput in) throws IOException {
		final long sizeOfCollection = in.readLong();
		for (long i = 0; i < sizeOfCollection; ++i) {
			final String key = in.readUTF();
			final Boolean value = in.readBoolean();
			booleans.put(key, value);
		}
	}

	private void readDoubles(DataInput in) throws IOException {
		final long sizeOfCollection = in.readLong();
		for (long i = 0; i < sizeOfCollection; ++i) {
			final String key = in.readUTF();
			final Double value = in.readDouble();
			doubles.put(key, value);
		}
	}
}
