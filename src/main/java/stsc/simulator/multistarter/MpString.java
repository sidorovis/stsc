package stsc.simulator.multistarter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MpString implements MpIterator<String> {
	private final String name;
	private final List<String> domen;
	int index;

	public MpString(String name, final String singleElement) throws BadParameterException {
		this(name, Arrays.asList(new String[] { singleElement }));
	}

	public MpString(String name, final List<String> domen) throws BadParameterException {
		super();
		this.name = name;
		if (domen.isEmpty())
			throw new BadParameterException("String parameter should have at least one element: " + name);
		this.domen = new ArrayList<String>(domen);
		this.index = 0;
	}

	@Override
	public long size() {
		return domen.size();
	}

	@Override
	public String toString() {
		if (index < domen.size())
			return name + "(" + domen.toString() + ")[" + domen.get(index) + "]";
		else
			return name + "(" + domen.toString() + ")[ END ]";
	}

	@Override
	public boolean hasNext() {
		return index < domen.size();
	}

	@Override
	public Parameter<String> currentParameter() {
		return new Parameter<String>(name, current());
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

}
