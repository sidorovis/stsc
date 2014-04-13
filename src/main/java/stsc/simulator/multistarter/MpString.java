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
	public String toString() {
		return name + "(" + domen.toString() + ")[" + domen.get(index) + "]";
	}

	@Override
	public boolean hasNext() {
		return index < domen.size();
	}

	@Override
	public Parameter<String> current() {
		return new Parameter<String>(name, domen.get(index));
	}

	@Override
	public void reset() {
		index = 0;
	}

	@Override
	public void increment() {
		index += 1;
	}

}
