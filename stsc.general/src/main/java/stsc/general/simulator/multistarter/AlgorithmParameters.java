package stsc.general.simulator.multistarter;

import java.util.Iterator;

public final class AlgorithmParameters {

	private final ParameterList<Integer> integers;
	private final ParameterList<Double> doubles;
	private final ParameterList<String> strings;
	private final ParameterList<String> subExecutions;

	private final ParameterList<?>[] parameters;

	public AlgorithmParameters(final AlgorithmParameters copy) {
		this.integers = copy.integers.clone();
		this.doubles = copy.doubles.clone();
		this.strings = copy.strings.clone();
		this.subExecutions = copy.subExecutions.clone();
		this.parameters = new ParameterList<?>[] { integers, doubles, strings, subExecutions };
	}

	public AlgorithmParameters() {
		this.integers = new ParameterList<Integer>();
		this.doubles = new ParameterList<Double>();
		this.strings = new ParameterList<String>();
		this.subExecutions = new ParameterList<String>();
		this.parameters = new ParameterList<?>[] { integers, doubles, strings, subExecutions };
	}

	public void reset() {
		for (ParameterList<?> list : parameters) {
			list.reset();
		}
	}

	public long size() {
		long result = 1;
		for (ParameterList<?> pl : parameters) {
			result *= pl.size();
		}
		return result;
	}

	public int parametersSize() {
		int size = 0;
		for (ParameterList<?> i : parameters) {
			size += i.getParams().size();
		}
		return size;
	}

	@Override
	public String toString() {
		String result = "";
		for (ParameterList<?> p : parameters) {
			result += "\n" + p.toString();
		}
		return result;
	}

	public ParameterList<?> getParamsFor(int i) {
		return parameters[i];
	}

	public ParameterList<Integer> getIntegers() {
		return integers;
	}

	public ParameterList<Double> getDoubles() {
		return doubles;
	}

	public ParameterList<String> getStrings() {
		return strings;
	}

	public ParameterList<String> getSubExecutions() {
		return subExecutions;
	}

	public ParameterList<?>[] getParameters() {
		return parameters;
	}

	public Iterator<MpIterator<String>> getSubExecutionIterator() {
		return getSubExecutions().getParams().iterator();
	}

}
