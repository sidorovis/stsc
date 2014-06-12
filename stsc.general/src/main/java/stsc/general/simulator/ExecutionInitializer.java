package stsc.general.simulator;

import stsc.common.algorithms.AlgorithmSettings;
import stsc.general.simulator.multistarter.ResetableIterable;
import stsc.general.simulator.multistarter.ResetableIterator;
import stsc.general.simulator.multistarter.grid.AlgorithmSettingsGridIterator;

public class ExecutionInitializer implements ResetableIterator<AlgorithmSettings>, ResetableIterable<AlgorithmSettings>, Cloneable {
	public String executionName;
	public String algorithmName;
	public AlgorithmSettingsGridIterator.Element iterator;

	public ExecutionInitializer(String eName, String algorithmName, AlgorithmSettingsGridIterator mas) {
		super();
		this.executionName = eName;
		this.algorithmName = algorithmName;
		this.iterator = mas.iterator();
	}

	private ExecutionInitializer(String eName, String algorithmName, AlgorithmSettingsGridIterator.Element iterator) {
		super();
		this.executionName = eName;
		this.algorithmName = algorithmName;
		this.iterator = iterator;
	}

	public ExecutionInitializer clone() {
		return new ExecutionInitializer(executionName, algorithmName, iterator.clone());
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
