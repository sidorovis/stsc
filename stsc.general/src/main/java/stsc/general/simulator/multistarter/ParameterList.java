package stsc.general.simulator.multistarter;

import java.util.ArrayList;
import java.util.List;

public class ParameterList implements Cloneable {

	public final ParameterType type;
	private final List<MpIterator<?>> params;
	private int index;

	public ParameterList(ParameterType type) {
		this.type = type;
		this.params = new ArrayList<MpIterator<?>>();
		this.index = 0;
	}

	public ParameterList clone() {
		return new ParameterList(this.type, this.params);
	}

	private ParameterList(final ParameterType type, final List<MpIterator<?>> params) {
		this.type = type;
		this.params = new ArrayList<MpIterator<?>>();
		for (MpIterator<?> mpIterator : params) {
			this.params.add(mpIterator.clone());
		}
		this.index = 0;
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
