package stsc.simulator.multistarter;

public interface MpIterator<T> extends ResetableIterator<T>, Cloneable {

	public Parameter<T> currentParameter();

	public void increment();

	public long size();

	public T parameter(int index);

	public MpIterator<T> clone();
}
