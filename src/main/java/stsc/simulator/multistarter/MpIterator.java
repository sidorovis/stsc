package stsc.simulator.multistarter;

public interface MpIterator<T> {
	public Parameter<T> current();
	public boolean hasNext();
	public void increment();
	public void reset();
}
