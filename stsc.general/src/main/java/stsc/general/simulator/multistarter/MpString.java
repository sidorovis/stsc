package stsc.general.simulator.multistarter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MpString extends MpIterator<String> {

	private final List<String> domen;
	int index;

	public MpString(String name, final String singleElement) throws BadParameterException {
		this(name, Arrays.asList(new String[] { singleElement }));
	}

	public MpString(String name, final String[] elements) throws BadParameterException {
		this(name, Arrays.asList(elements));
	}

	public MpString(String name, final List<String> domen) throws BadParameterException {
		super(name);
		if (domen.isEmpty())
			throw new BadParameterException("String parameter should have at least one element: " + name);
		this.domen = domen;
		Collections.sort(this.domen);
		this.index = 0;
	}

	@Override
	public MpIterator<String> clone() {
		return new MpString(getName(), domen, true);
	}

	private MpString(String name, final List<String> domen, boolean privateBoolean) {
		super(name);
		this.domen = domen;
		this.index = 0;
	}

	@Override
	public long size() {
		return domen.size();
	}

	@Override
	public String toString() {
		if (index < domen.size()) {
			if (1 == domen.size())
				return getName() + "(" + domen.toString() + ")";
			else
				return getName() + "(" + domen.toString() + ")[" + domen.get(index) + "]";
		} else {
			return getName() + "(" + domen.toString() + ")[ END ]";
		}
	}

	@Override
	public boolean hasNext() {
		return index < domen.size();
	}

	@Override
	public void reset() {
		index = 0;
	}

	@Override
	public void increment() {
		index += 1;
	}

	@Override
	public String next() {
		String result = current();
		increment();
		return result;
	}

	@Override
	public void remove() {
	}

	@Override
	public String current() {
		return domen.get(index);
	}

	@Override
	public String parameter(int index) {
		return domen.get(index);
	}

	@Override
	public int getIndexByValue(String value) {
		return Collections.binarySearch(domen, value);
	}
}
