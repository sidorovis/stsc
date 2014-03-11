package stsc.algorithms.primitive;

import stsc.algorithms.EodSignal;


public class TestingEodAlgorithmSignal extends EodSignal {
	public final String dateRepresentation;

	public TestingEodAlgorithmSignal(String dateRepresentation) {
		this.dateRepresentation = dateRepresentation;
	}
}