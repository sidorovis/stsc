package stsc.algorithms.eod.primitive;

import stsc.common.signals.StockSignal;


public class TestingEodAlgorithmSignal extends StockSignal {
	public final String dateRepresentation;

	public TestingEodAlgorithmSignal(String dateRepresentation) {
		this.dateRepresentation = dateRepresentation;
	}
}