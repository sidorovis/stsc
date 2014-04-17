package stsc.simulator.multistarter;

public interface MpIterator<T> extends ResetableIterator<T> {

	public Parameter<T> currentParameter();
	public void increment();
	public long size();
}
