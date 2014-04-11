package stsc.simulator.multistarter;

import java.util.Iterator;

public class MultiParameter<T> implements Iterable<Parameter<T>> {

	final private MpIterator<T> multiParameter;

	public MultiParameter(final MpIterator<T> multiParameter) {
		this.multiParameter = multiParameter;
	}

	@Override
	public Iterator<Parameter<T>> iterator() {
		return multiParameter;
	}

	public void reset() {
		multiParameter.reset();
	}
}
