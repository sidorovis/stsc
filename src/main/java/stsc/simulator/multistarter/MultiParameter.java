package stsc.simulator.multistarter;

public class MultiParameter<T> implements Iterable<T> {

	final private MPIterator<T> multiParameter;

	public MultiParameter(final MPIterator<T> multiParameter) {
		this.multiParameter = multiParameter;
	}

	@Override
	public MPIterator<T> iterator() {
		return multiParameter;
	}

	public void reset() {
		multiParameter.reset();
	}
}
