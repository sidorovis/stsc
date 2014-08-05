package stsc.general.simulator.multistarter;

import java.util.ArrayList;
import java.util.List;

public class ParameterList<Type> implements Cloneable {

	private final List<MpIterator<Type>> params;
	private int index;

	public ParameterList() {
		this.params = new ArrayList<MpIterator<Type>>();
		this.index = 0;
	}

	public ParameterList<Type> clone() {
		return new ParameterList<Type>(this.params);
	}

	private ParameterList(final List<MpIterator<Type>> params) {
		this.params = new ArrayList<MpIterator<Type>>(params.size());
		for (MpIterator<Type> mpIterator : params) {
			this.params.add(mpIterator.clone());
		}
		this.index = 0;
	}

	public void add(final MpIterator<Type> mpIterator) {
		params.add(mpIterator);
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

	public MpIterator<Type> getCurrentParam() {
		return params.get(index);
	}

	public List<MpIterator<Type>> getParams() {
		return params;
	}

	@Override
	public String toString() {
		return String.valueOf(index) + ": " + params.toString();
	}

	public long size() {
		long result = 1;
		for (MpIterator<?> i : params) {
			result *= i.size();
		}
		return result;
	}
}
