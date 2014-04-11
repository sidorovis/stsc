package stsc.simulator.multistarter;

import java.util.List;

public class MultiParameterBuilder {

	public static MultiParameter<Integer> Integer(Integer from, Integer to, Integer step) {
		return new MultiParameter<Integer>(new MpInteger("n", from, to, step));
	}

	public static MultiParameter<Double> Double(Double from, Double to, Double step) {
		return new MultiParameter<Double>(new MpDouble("v", from, to, step));
	}

	public static MultiParameter<String> String(final List<String> domen) {
		return new MultiParameter<String>(new MpString("sd", domen));
	}

	public static MultiParameter<String> SubExecution(final List<String> domen) {
		return new MultiParameter<String>(new MpSubExecution("se", domen));
	}

}
