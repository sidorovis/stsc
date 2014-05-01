package stsc.simulator;

import stsc.algorithms.AlgorithmSettings;
import stsc.simulator.multistarter.ResetableIterable;
import stsc.simulator.multistarter.ResetableIterator;
import stsc.simulator.multistarter.grid.AlgorithmSettingsGridIterator;

public class ExecutionInitializer implements ResetableIterator<AlgorithmSettings>, ResetableIterable<AlgorithmSettings> {
	public String executionName;
	public String algorithmName;
	public AlgorithmSettingsGridIterator.Element iterator;

	public ExecutionInitializer(String eName, String algorithmName, AlgorithmSettingsGridIterator mas) {
		super();
		this.executionName = eName;
		this.algorithmName = algorithmName;
		this.iterator = mas.iterator();
	}

	public void reset() {
		iterator.reset();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public AlgorithmSettings next() {
		return iterator.next();
	}

	@Override
	public void remove() {
		iterator.remove();
	}

	@Override
	public String toString() {
		return executionName + "(" + algorithmName + ")\n" + iterator.current() + "\n";
	}

	@Override
	public AlgorithmSettings current() {
		return iterator.current();
	}

	@Override
	public ResetableIterator<AlgorithmSettings> iterator() {
		return this;
	}
}
