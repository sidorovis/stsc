package stsc.simulator.multistarter;

import java.util.Iterator;

public class ExecutionIterator<ExecutionType> implements Iterable<ExecutionType> {
	private final Iterator<ExecutionType> execution;

	public ExecutionIterator(Iterator<ExecutionType> execution) {
		this.execution = execution;
	}

	@Override
	public Iterator<ExecutionType> iterator() {
		return execution;
	}

}
