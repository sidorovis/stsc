package stsc.general.simulator.multistarter;

import java.util.Iterator;

public interface ResetableIterator<E> extends Iterator<E> {
	public E current();
	public void reset();
}
