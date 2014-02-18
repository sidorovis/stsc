package stsc.algorithms;

import stsc.storage.ExecutionSignal;

public class TestAlgorithmSignal extends ExecutionSignal {
	public final String dateRepresentation;

	public TestAlgorithmSignal(String dateRepresentation) {
		this.dateRepresentation = dateRepresentation;
	}
}