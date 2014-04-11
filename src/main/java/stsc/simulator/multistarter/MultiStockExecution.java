package stsc.simulator.multistarter;

import java.util.ArrayList;

import stsc.algorithms.StockAlgorithm;

public class MultiStockExecution {
	final StockAlgorithm algorithm;
	final ArrayList<MultiParameter<MpInteger>> intParameters = new ArrayList<>();
	final ArrayList<MultiParameter<MpDouble>> doubleParameters = new ArrayList<>();
	final ArrayList<MultiParameter<MpString>> stringParameters = new ArrayList<>();
	final ArrayList<MultiParameter<MpSubExecution>> subExecutionParameters = new ArrayList<>();

	public MultiStockExecution(final StockAlgorithm stockAlgorithm) {
		this.algorithm = stockAlgorithm;
	}

}
