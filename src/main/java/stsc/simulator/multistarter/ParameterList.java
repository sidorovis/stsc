package stsc.simulator.multistarter;

import java.util.ArrayList;
import java.util.List;

class ParameterList {
	private final List<MpIterator<?>> params = new ArrayList<MpIterator<?>>();
	private int index;
	public final ParameterType type;

	public ParameterList(ParameterType type) {
		this.type = type;
		index = 0;
	}

	public void add(final MpIterator<?> mpi) {
		params.add(mpi);
	}

	public void reset() {
		index = 0;
	}

	public void increment() {
		index += 1;
	}

	public boolean empty() {
		return params.isEmpty();
	}

	public boolean hasNext() {
		return index + 1 < params.size();
	}

	public boolean hasCurrent() {
		return index < params.size();
	}

	public MpIterator<?> getCurrentParam() {
		return params.get(index);
	}

	public List<MpIterator<?>> getParams() {
		return params;
	}

	@Override
	public String toString() {
		return String.valueOf(index) + ": " + params.toString();
	}
}
