package stsc.simulator.multistarter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MpString implements MpIterator<String> {
	private final String name;
	private final List<String> domen;
	private Iterator<String> iterator;

	public MpString(String name, final List<String> domen) {
		super();
		this.name = name;
		this.domen = new ArrayList<String>(domen);
		this.iterator = this.domen.iterator();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public Parameter<String> next() {
		final Parameter<String> result = new Parameter<>(name, iterator.next());
		return result;
	}

	@Override
	public void remove() {
	}

	@Override
	public void reset() {
		iterator = this.domen.iterator();
	}

}
