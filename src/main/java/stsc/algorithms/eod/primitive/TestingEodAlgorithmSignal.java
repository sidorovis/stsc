package stsc.algorithms.eod.primitive;

import stsc.signals.EodSignal;


public class TestingEodAlgorithmSignal extends EodSignal {
	public final String dateRepresentation;

	public TestingEodAlgorithmSignal(String dateRepresentation) {
		this.dateRepresentation = dateRepresentation;
	}
}