package stsc.simulator.multistarter;

import java.util.List;

public class MultiParameters {

	public static MultiParameter<Integer> Integer(Integer from, Integer to, Integer step) {
		return new MultiParameter<Integer>(new MPInteger(from, to, step));
	}

	public static MultiParameter<Double> Double(Double from, Double to, Double step) {
		return new MultiParameter<Double>(new MPDouble(from, to, step));
	}

	public static MultiParameter<String> String(final List<String> domen) {
		return new MultiParameter<String>(new MPString(domen));
	}

	public static MultiParameter<String> SubExecution(final List<String> domen) {
		return new MultiParameter<String>(new MPSubExecution(domen));
	}

}
