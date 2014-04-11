package stsc.simulator.multistarter;

import java.util.Iterator;

public interface MpIterator<T> extends Iterator<Parameter<T>> {
	public void reset();
}
