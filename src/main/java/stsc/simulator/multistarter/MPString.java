package stsc.simulator.multistarter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MPString implements MPIterator<String> {
	private final List<String> domen;
	private Iterator<String> iterator;

	public MPString(final List<String> domen) {
		super();
		this.domen = new ArrayList<String>(domen);
		this.iterator = this.domen.iterator();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public String next() {
		return iterator.next();
	}

	@Override
	public void remove() {
	}

	@Override
	public void reset() {
		iterator = this.domen.iterator();
	}

}
