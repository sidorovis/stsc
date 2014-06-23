package stsc.general.simulator.multistarter;

public abstract class MpIterator<T> implements ResetableIterator<T>, Cloneable {

	private final String name;

	protected MpIterator(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Parameter<T> currentParameter() {
		return new Parameter<T>(getName(), current());
	}

	public abstract void increment();

	public abstract long size();

	public abstract T parameter(int index);

	public abstract MpIterator<T> clone();

	public T getRangom() {
		return parameter((int) (Math.random() * size()));
	}
}
